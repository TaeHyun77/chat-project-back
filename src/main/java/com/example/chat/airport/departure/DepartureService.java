package com.example.chat.airport.departure;

import com.example.chat.airport.departure.dto.DepartureResDto;
import com.example.chat.airport.kafka.message.CongestionMessage;
import com.example.chat.airport.departure.repository.DepartureRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DepartureService {

    // 혼잡도 합계 변화 임계값 (이 값 이상 변화 시 이벤트 발행)
    private static final long CONGESTION_THRESHOLD = 50;

    private static final String TOPIC_CONGESTION = "airport.congestion.changed";

    private final DepartureRepository departureRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /*
     * 공항 출국장 현황 데이터를 DB에 갱신
     * */
    @Transactional
    public void upsertDepartureData(JsonNode departureJsonData) {
        for (JsonNode item : departureJsonData) {
            String date = item.path("adate").asText();
            String timeZone = item.path("atime").asText();

            if (date.equals("합계")) continue;

            long newT1d1 = item.path("t1dg1").asLong();
            long newT1d2 = item.path("t1dg2").asLong();
            long newT1d3 = item.path("t1dg3").asLong();
            long newT1d4 = item.path("t1dg4").asLong();
            long newT1d5 = item.path("t1dg5").asLong();
            long newT1d6 = item.path("t1dg6").asLong();
            long newT2d1 = item.path("t2dg1").asLong();
            long newT2d2 = item.path("t2dg2").asLong();

            long newT1Sum = newT1d1 + newT1d2 + newT1d3 + newT1d4 + newT1d5 + newT1d6;
            long newT2Sum = newT2d1 + newT2d2;

            departureRepository.findByDateAndTimeZone(date, timeZone)
                    // 이미 존재하는 데이터라면, 더티 체킹
                    .ifPresentOrElse(
                            exists -> {
                                long prevT1Sum = exists.getT1Depart1() + exists.getT1Depart2()
                                        + exists.getT1Depart3() + exists.getT1Depart4()
                                        + exists.getT1Depart5() + exists.getT1Depart6();
                                long prevT2Sum = exists.getT2Depart1() + exists.getT2Depart2();

                                exists.updateDeparture(
                                        newT1d1, newT1d2, newT1d3, newT1d4, newT1d5, newT1d6,
                                        newT2d1, newT2d2
                                );

                                // 임계값 이상 변화 시 Kafka 메시지 수집
                                // 임계값 이상 변화 시 Kafka 메시지 즉시 발행
                                if (Math.abs(newT1Sum - prevT1Sum) >= CONGESTION_THRESHOLD
                                        || Math.abs(newT2Sum - prevT2Sum) >= CONGESTION_THRESHOLD) {
                                    kafkaTemplate.send(TOPIC_CONGESTION, date + "_" + timeZone,
                                            CongestionMessage.builder()
                                                    .date(date)
                                                    .timeZone(timeZone)
                                                    .t1Depart1(newT1d1)
                                                    .t1Depart2(newT1d2)
                                                    .t1Depart3(newT1d3)
                                                    .t1Depart4(newT1d4)
                                                    .t1Depart5(newT1d5)
                                                    .t1Depart6(newT1d6)
                                                    .t2Depart1(newT2d1)
                                                    .t2Depart2(newT2d2)
                                                    .prevT1Sum(prevT1Sum)
                                                    .newT1Sum(newT1Sum)
                                                    .prevT2Sum(prevT2Sum)
                                                    .newT2Sum(newT2Sum)
                                                    .build());
                                }
                            },
                            // 신규 데이터라면, 저장
                            () -> departureRepository.save(Departure.builder()
                                    .date(date)
                                    .timeZone(timeZone)
                                    .t1Depart1(newT1d1)
                                    .t1Depart2(newT1d2)
                                    .t1Depart3(newT1d3)
                                    .t1Depart4(newT1d4)
                                    .t1Depart5(newT1d5)
                                    .t1Depart6(newT1d6)
                                    .t2Depart1(newT2d1)
                                    .t2Depart2(newT2d2)
                                    .build())
                    );
        }
    }

    // 모든 출국장 데이터 조회
    public List<DepartureResDto> getDepartures() {

        List<Departure> departures = departureRepository.findAll();

        return departures.stream()
                .map(DepartureResDto::from)
                .collect(Collectors.toList());
    }

    // 어제 출국장 데이터 삭제
    @Transactional
    public void cleanUpDepartureData() {
        String yesterday = LocalDate.now().minusDays(1).format(formatter);

        departureRepository.deleteByDate(yesterday);
    }
}
