package com.example.chat.airport.plane;

import com.example.chat.config.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Plane extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String searchDate;

    private String flightId;

    private String airLine;

    // 인천 공항 오기 전 출발한 곳
    private String airport;

    // airPort의 코드
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

    @Builder
    public Plane(String searchDate, String flightId, String airLine, String airport, String airportCode, String scheduleDateTime, String estimatedDateTime, String gatenumber, String terminalid, String remark, String codeShare, String chkinrange) {
        this.searchDate = searchDate;
        this.flightId = flightId;
        this.airLine = airLine;
        this.airport = airport;
        this.airportCode = airportCode;
        this.scheduleDateTime = scheduleDateTime;
        this.estimatedDateTime = estimatedDateTime;
        this.gatenumber = gatenumber;
        this.terminalid = terminalid;
        this.remark = remark;
        this.codeShare = codeShare;
        this.chkinrange = chkinrange;
    }

    public void updatePlane(String remark, String estimatedDateTime, String gatenumber, String terminalid) {
        this.remark = remark;
        this.estimatedDateTime = estimatedDateTime;
        this.gatenumber = gatenumber;
        this.terminalid = terminalid;
    }
}
