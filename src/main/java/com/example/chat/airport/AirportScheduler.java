package com.example.chat.airport;

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

    private final AirService airService;

    /*
    두 데이터 모두 실시간으로 변경될 수 있기에 짧은 주기로 스케줄링을 진행해야 함 ( 5분? )
    */

    @Scheduled(fixedDelay = 300000) // 5분마다 진행
    public void runDepartureData() {
        try {
            airService.getArrivalsData();
            log.info("출국장 데이터 불러오기 완료");
        } catch (ChatException e) {
            log.info("출국장 데이터 불러오기 실패");
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_ARRIVAL_DATA);
        }
    }

    @Scheduled(fixedDelay = 300000)
    public void runPlaneData() {
        try {
            airService.getPlane();
            log.info("항공편 데이터 불러오기 완료");
        } catch (ChatException e) {
            log.info("항공편 데이터 불러오기 실패");
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_PLANE_DATA);
        }
    }
}
