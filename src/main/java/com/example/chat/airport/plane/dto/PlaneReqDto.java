package com.example.chat.airport.plane.dto;

import com.example.chat.airport.plane.Plane;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaneReqDto {

    private String searchDate;

    private String flightId;

    private String airLine;

    // 인천 공항 오기 전 출발한 곳
    private String airport;

    // airport의 코드
    private String airportCode;

    // 출발 시각
    private String scheduleDateTime;

    // 실제 출발/도착 시간이 아닌 예상된 시간
    private String estimatedDateTime;

    private String gatenumber;

    // 몇 터미널 인지
    private String terminalid;

    // 상태
    private String remark;

    private String codeShare;

    private String chkinrange;
}
