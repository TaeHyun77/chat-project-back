package com.example.chat.airport.dto;

import com.example.chat.airport.entity.Plane;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaneDto {

    private String flightId;

    private String airLine;

    // 인천 공항 오기 전 출발한 곳
    private String airport;

    // airport의 코드
    private String airportCode;

    // 출발 시각
    private String scheduleDatetime;

    // 실제 출발/도착 시간이 아닌 예상된 시간
    private String estimatedDatetime;

    private String gateNumber;

    // 몇 터미널 인지
    private String terminalId;

    // 상태
    private String remark;

    // 항공기 식별 번호
    private String aircraftRegNo;

    private String codeShare;

    @Builder
    public PlaneDto(String flightId, String airLine, String airport, String airportCode, String scheduleDatetime, String estimatedDatetime, String gateNumber, String terminalId, String remark, String aircraftRegNo, String codeShare) {
        this.flightId = flightId;
        this.airLine = airLine;
        this.airport = airport;
        this.airportCode = airportCode;
        this.scheduleDatetime = scheduleDatetime;
        this.estimatedDatetime = estimatedDatetime;
        this.gateNumber = gateNumber;
        this.terminalId = terminalId;
        this.remark = remark;
        this.aircraftRegNo = aircraftRegNo;
        this.codeShare = codeShare;
    }

    public Plane toPlane() {
        return Plane.builder()
                .flightId(flightId)
                .airLine(airLine)
                .airport(airport)
                .airportCode(airportCode)
                .scheduleDatetime(scheduleDatetime)
                .estimatedDatetime(estimatedDatetime)
                .gateNumber(gateNumber)
                .terminalId(terminalId)
                .remark(remark)
                .aircraftRegNo(aircraftRegNo)
                .codeShare(codeShare)
                .build();
    }
}
