package com.example.chat.airport.entity;

import com.example.chat.config.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Plane extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String flightId;

    private String airLine;

    // 인천 공항 오기 전 출발한 곳
    private String airport;

    // airPort의 코드
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
    public Plane(String flightId, String airLine, String airport, String airportCode, String scheduleDatetime, String estimatedDatetime, String gateNumber, String terminalId, String remark, String aircraftRegNo, String codeShare) {
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

    public void updatePlane(String remark, String estimatedDatetime, String gateNumber, String terminalId) {
        this.remark = remark;
        this.estimatedDatetime = estimatedDatetime;
        this.gateNumber = gateNumber;
        this.terminalId = terminalId;
    }
}
