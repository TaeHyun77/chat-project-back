package com.example.chat.airport.Departure;

import com.example.chat.airport.Departure.dto.DepartureResDto;
import com.example.chat.airport.Departure.repository.DepartureRepository;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DepartureService {

    private final DepartureRepository departureRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /*
     * 공항 출국장 현황 데이터를 DB에 갱신
     * */
    @Transactional
    public void upsertDepartureData(JsonNode departureJsonData) {
        try {

            for (JsonNode item : departureJsonData) {

                String date = item.path("adate").asText();
                String timeZone = item.path("atime").asText();

                if (date.equals("합계")) continue;

                Departure departure = Departure.builder()
                        .date(date)
                        .timeZone(item.path("atime").asText())
                        .t1Depart1(item.path("t1dg1").asLong())
                        .t1Depart2(item.path("t1dg2").asLong())
                        .t1Depart3(item.path("t1dg3").asLong())
                        .t1Depart4(item.path("t1dg4").asLong())
                        .t1Depart5(item.path("t1dg5").asLong())
                        .t1Depart6(item.path("t1dg6").asLong())
                        .t2Depart1(item.path("t2dg1").asLong())
                        .t2Depart2(item.path("t2dg2").asLong())
                        .build();

                departureRepository.findByDateAndTimeZone(date, timeZone)
                        .ifPresentOrElse(
                                exists -> exists.updateDeparture(departure), // 더티 체킹
                                () -> departureRepository.save(departure) // 신규 저장
                        );
            }
        } catch (Exception e) {
            log.error("출국장 데이터 저장 중 예외 발생: {}", e.getMessage());
            throw e;
        }
    }

    // 모든 출국장 데이터 조회
    public List<DepartureResDto> getDepartures() {

        List<Departure> departures = departureRepository.findAllCustom();

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
