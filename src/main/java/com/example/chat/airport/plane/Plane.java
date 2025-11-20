package com.example.chat.airport.plane;

import com.example.chat.config.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    // 예정 출발 시각 ( 고정 )
    private String scheduleDateTime;

    // 변경된 출발 시각 ( 유동 )
    private String estimatedDateTime;

    // 게이트 번호
    private String gatenumber;

    // 출발 터미널
    private String terminalid;

    // 상태
    private String remark;

    private String codeShare;

    // 체크인 구역
    private String chkinrange;

    public void updatePlane(String remark, String estimatedDateTime, String gatenumber, String terminalid, String chkinrange) {
        this.remark = remark;
        this.estimatedDateTime = estimatedDateTime;
        this.gatenumber = gatenumber;
        this.terminalid = terminalid;
        this.chkinrange = chkinrange;
    }
}
