package com.example.chat.airport.plane.dto;

import com.example.chat.airport.plane.Plane;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public static PlaneResDto from(Plane plane){
        return PlaneResDto.builder()
                .flightId(plane.getFlightId())
                .airLine(plane.getAirLine())
                .airport(plane.getAirport())
                .airportCode(plane.getAirportCode())
                .scheduleDateTime(plane.getScheduleDateTime())
                .estimatedDateTime(plane.getEstimatedDateTime())
                .gatenumber(plane.getGatenumber())
                .terminalid(plane.getTerminalid())
                .remark(plane.getRemark())
                .chkinrange(plane.getChkinrange())
                .build();
    }
}

