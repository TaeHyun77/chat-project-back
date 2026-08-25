package com.example.chat.airport.departure;

import com.example.chat.airport.departure.dto.DepartureResDto;
import com.example.chat.airport.departure.event.CongestionAlertEvent;
import com.example.chat.airport.departure.repository.DepartureRepository;
import com.example.chat.airport.departure.vo.T1GateStatus;
import com.example.chat.airport.departure.vo.T2GateStatus;
import com.example.chat.common.DateUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DepartureService {
    // 혼잡도 합계 변화 임계값 (이 값 이상 변화 시 이벤트 발행 - 공식 값)
    private static final long BUSY_THRESHOLD = 8200;

    private final DepartureRepository departureRepository;
    private final ApplicationEventPublisher eventPublisher;

    // 공항 출국장 현황 데이터를 DB에 갱신
    @Transactional
    public void upsertDepartureData(JsonNode departureJsonData) {
        for (JsonNode item : departureJsonData) {
            String date = item.path("adate").asText();
            String timeZone = item.path("atime").asText();

            // 합계 데이터는 건너띔
            if (date.equals("합계")) continue;

            T1GateStatus newT1Gates = new T1GateStatus(
                    item.path("t1dg1").asLong(),
                    item.path("t1dg2").asLong(),
                    item.path("t1dg3").asLong(),
                    item.path("t1dg4").asLong(),
                    item.path("t1dg5").asLong(),
                    item.path("t1dg6").asLong()
            );
            T2GateStatus newT2Gates = new T2GateStatus(
                    item.path("t2dg1").asLong(),
                    item.path("t2dg2").asLong()
            );

            departureRepository.findByDateAndTimeZone(date, timeZone)
                    .ifPresentOrElse(
                            // 이미 존재하는 경우 update
                            exists -> updateWithCongestionCheck(exists, date, timeZone, newT1Gates, newT2Gates),
                            () -> departureRepository.save(Departure.builder()
                                    .date(date)
                                    .timeZone(timeZone)
                                    .t1Gates(newT1Gates)
                                    .t2Gates(newT2Gates)
                                    .build())
                    );
        }
    }

    // update & 조건에 맞으면 혼잡도 알림
    private void updateWithCongestionCheck(Departure exists, String date, String timeZone,
                                            T1GateStatus newT1Gates, T2GateStatus newT2Gates) {
        boolean prevT1Busy = exists.getT1Gates().isBusy(BUSY_THRESHOLD);
        boolean prevT2Busy = exists.getT2Gates().isBusy(BUSY_THRESHOLD);

        exists.updateDeparture(newT1Gates, newT2Gates);

        // 터미널별로 독립적으로 판단 - 혼잡 상태로 진입한 터미널의 구독자에게만 발송
        if (!prevT1Busy && newT1Gates.isBusy(BUSY_THRESHOLD)) {
            publishCongestionAlert(date, timeZone, "T1", newT1Gates, newT2Gates);
        }
        if (!prevT2Busy && newT2Gates.isBusy(BUSY_THRESHOLD)) {
            publishCongestionAlert(date, timeZone, "T2", newT1Gates, newT2Gates);
        }
    }

    // 커밋 이후 리스너에서 혼잡 터미널 구독자에게 알림 메일 발송
    private void publishCongestionAlert(String date, String timeZone, String busyTerminal,
                                        T1GateStatus newT1Gates, T2GateStatus newT2Gates) {
        eventPublisher.publishEvent(
                CongestionAlertEvent.builder()
                        .date(date)
                        .timeZone(timeZone)
                        .busyTerminal(busyTerminal)
                        .t1Gates(newT1Gates)
                        .t2Gates(newT2Gates)
                        .build());
    }

    // 모든 출국장 데이터 조회
    public List<DepartureResDto> getDepartures() {
        List<Departure> departures = departureRepository.findAll();

        return departures.stream()
                .map(DepartureResDto::from)
                .collect(Collectors.toList());
    }

    // 자정에 오늘 이전의 출국장 데이터 삭제
    @Transactional
    public void cleanUpDepartureData() {
        String today = LocalDate.now().format(DateUtils.BASIC_DATE);

        departureRepository.deleteByDateBefore(today);
    }
}
