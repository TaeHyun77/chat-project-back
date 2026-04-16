package com.example.chat.airport;

import com.example.chat.airport.departure.DepartureService;
import com.example.chat.airport.plane.PlaneService;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class AirportScheduler {
    private final DepartureService departureService;
    private final PlaneService planeService;
    private final ApiService apiService;

//    // 항공편 데이터 동기화 (1분마다)
//    @Scheduled(fixedDelay = 60000)
//    public void syncPlaneData() {
//        try {
//            apiService.getApiPlane();
//            log.debug("항공편 데이터 API 호출 완료");
//        } catch (Exception e) {
//            log.error("항공편 데이터 API 호출 실패", e);
//            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_CALL_PLANE_API);
//        }
//    }

    // 출국장 데이터 동기화 (3분마다)
    @Scheduled(fixedDelay = 180000)
    public void syncDepartureData() {
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
    public void syncParkingData() {
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

    // 매 자정에 유효하지 않은 항공편 삭제 ( searchDate 값이 이틀 전이고, remark 값이 "출발"인 데이터 삭제 )
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanUpExpiredPlaneData() {
        try {
            planeService.cleanUpPlaneDataOlderThanTwoDays();
            log.debug("유효하지 않은 항공편 삭제 완료");
        } catch (Exception e) {
            log.error("유효하지 않은 항공편 삭제 실패", e);
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.FAIL_TO_DELETE_PLANE_DATA);
        }
    }
}
