package com.example.chat.airport;

import com.example.chat.airport.departure.application.DepartureService;
import com.example.chat.airport.plane.application.PlaneService;
import com.example.chat.common.DateUtils;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@RequiredArgsConstructor
@Slf4j
@Component
public class AirportScheduler {
    private final DepartureService departureService;
    private final PlaneService planeService;
    private final ApiService apiService;
    
    // scheduleDateTime 기준 API 특성상 어제 날짜로 조회해야 지연·결항 상태 변경을 감지할 수 있음 (10분마다)
    @Scheduled(fixedDelay = 600_000)
    public void trackDelayedFlightsFromYesterday() {
        String date = LocalDate.now().minusDays(1).format(DateUtils.BASIC_DATE);
        try {
            apiService.fetchAndUpdatePlaneStatus(date);
        } catch (Exception e) {
            log.error("{} 지연 항공편 상태 추적 실패", date, e);
        }
    }

    // 오늘 항공편 (2분마다)
    @Scheduled(fixedDelay = 120_000)
    public void syncTodayPlane() {
        String date = LocalDate.now().format(DateUtils.BASIC_DATE);
        syncPlane(date);
    }

    // 내일 항공편 (2분마다)
    @Scheduled(fixedDelay = 120_000)
    public void syncTomorrowPlane() {
        String date = LocalDate.now().plusDays(1).format(DateUtils.BASIC_DATE);
        syncPlane(date);
    }

    // 모레 항공편 (30분마다)
    @Scheduled(fixedDelay = 1_800_000)
    public void syncDayAfterTomorrowPlane() {
        String date = LocalDate.now().plusDays(2).format(DateUtils.BASIC_DATE);
        syncPlane(date);
    }

    private void syncPlane(String searchDate) {
        try {
            apiService.fetchAndSyncPlaneData(searchDate);
        } catch (Exception e) {
            log.error("{} 항공편 동기화 실패", searchDate, e);
        }
    }

    // 출국장 데이터 동기화 (5분마다)
    @Scheduled(fixedDelay = 300_000)
    public void syncDeparture() {
        try {
            apiService.getApiDeparture();
            log.debug("출국장 데이터 API 호출 완료");
        } catch (Exception e) {
            log.error("출국장 데이터 API 호출 실패", e);
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_CALL_DEPARTURE_API);
        }
    }

    // 주차장 데이터 동기화 (5분마다)
    @Scheduled(fixedDelay = 300000)
    public void syncParking() {
        try {
            apiService.getApiParking();
            log.debug("주차장 데이터 API 호출 완료");
        } catch (Exception e) {
            log.error("주차장 데이터 API 호출 실패", e);
        }
    }

    // 매 자정에 어제자 출국장 현황 데이터 삭제
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanUpExpiredDepartureData() {
        try {
            departureService.cleanUpDepartureData();
            log.debug("유효하지 않은 출국장 현황 데이터 삭제 완료");
        } catch (Exception e) {
            log.error("유효하지 않은 출국장 현황 데이터 삭제 실패", e);
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.FAIL_TO_DELETE_DEPARTURE_DATA);
        }
    }
}
