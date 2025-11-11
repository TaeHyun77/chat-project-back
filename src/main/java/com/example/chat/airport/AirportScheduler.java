package com.example.chat.airport;

import com.example.chat.airport.Departure.DepartureService;
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

    private final PlaneService planeService;
    private final DepartureService departureService;


    @Scheduled(fixedDelay = 300000) // 5분마다 진행
    public void runDepartureData() {
        try {
            departureService.getDepartureData();

            log.info("출국장 데이터 불러오기 완료");
        } catch (ChatException e) {
            log.info("출국장 데이터 불러오기 실패");

            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_ARRIVAL_DATA);
        }
    }

    @Scheduled(fixedDelay = 60000) // 1분마다 진행
    public void runPlaneData() {
        try {
            planeService.getPlaneData();

            log.info("항공편 데이터 불러오기 완료");
        } catch (ChatException e) {
            log.info("항공편 데이터 불러오기 실패");

            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_PLANE_DATA);
        }
    }

    // 매 자정에 어제자 출국장 현황 데이터 삭제
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteDepartureData() {
        try {
            departureService.cleanUpDepartureData();

            log.info("유효하지 않은 출국장 현황 데이터 삭제 완료");
        } catch (ChatException e) {
            log.info("유효하지 않은 출국장 현황 데이터 삭제 실패");

            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_DELETE_DEPARTURE_DATA);
        }
    }

    // 매 자정에 유효하지 않은 항공편 삭제 ( searchDate 값이 오늘, 내일, 모레이지 않고 remark 값이 "출발"인 데이터 삭제 )
    @Scheduled(cron = "0 0 0 * * *")
    public void deletePlaneData() {
        try {
            planeService.cleanUpPlaneData();

            log.info("유효하지 않은 항공편 삭제 완료");
        } catch (ChatException e) {
            log.info("유효하지 않은 항공편 삭제 실패");

            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_DELETE_PLANE_DATA);
        }
    }
}
