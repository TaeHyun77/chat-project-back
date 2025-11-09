package com.example.chat.airport.plane;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
public class PlaneResDto {

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

    @Builder
    public PlaneResDto(String flightId, String airLine, String airport, String airportCode, String scheduleDateTime, String estimatedDateTime, String gatenumber, String terminalid, String remark, String codeShare, String chkinrange) {
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
}

