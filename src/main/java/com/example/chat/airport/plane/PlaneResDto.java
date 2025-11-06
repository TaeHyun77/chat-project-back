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

    private String scheduleDatetime;

    private String estimatedDatetime;

    private String gateNumber;

    private String terminalId;

    private String remark;

    private String aircraftRegNo;

    private String codeShare;

    @Builder
    public PlaneResDto(String flightId, String airLine, String airport, String airportCode, String scheduleDatetime, String estimatedDatetime, String gateNumber, String terminalId, String remark, String aircraftRegNo, String codeShare) {
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
}

