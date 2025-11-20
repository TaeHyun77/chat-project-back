package com.example.chat.airport.plane;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaneResDto {

    private String searchDate;

    private String flightId;

    private String airLine;

    private String airport;

    private String airportCode;

    private String scheduleDateTime;

    private String estimatedDateTime;

    private String gatenumber;

    private String terminalid;

    private String remark;

    private String codeShare;

    private String chkinrange;

    public Plane toPlane() {
        return Plane.builder()
                .searchDate(this.searchDate)
                .flightId(this.flightId)
                .airLine(this.airLine)
                .airport(this.airport)
                .airportCode(this.airportCode)
                .scheduleDateTime(this.scheduleDateTime)
                .estimatedDateTime(this.estimatedDateTime)
                .gatenumber(this.gatenumber)
                .terminalid(this.terminalid)
                .remark(this.remark)
                .codeShare(this.codeShare)
                .chkinrange(this.chkinrange)
                .build();
    }
}

