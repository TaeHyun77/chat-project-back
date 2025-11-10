package com.example.chat.airport.plane;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PlaneDto {

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

    @Builder
    public PlaneDto(String searchDate, String flightId, String airLine, String airport, String airportCode, String scheduleDateTime, String estimatedDateTime, String gatenumber, String terminalid, String remark, String codeShare, String chkinrange) {
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

    public Plane toPlane() {
        return Plane.builder()
                .searchDate(searchDate)
                .flightId(flightId)
                .airLine(airLine)
                .airport(airport)
                .airportCode(airportCode)
                .scheduleDateTime(scheduleDateTime)
                .estimatedDateTime(estimatedDateTime)
                .gatenumber(gatenumber)
                .terminalid(terminalid)
                .remark(remark)
                .codeShare(codeShare)
                .chkinrange(chkinrange)
                .build();
    }
}
