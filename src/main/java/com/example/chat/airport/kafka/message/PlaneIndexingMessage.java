package com.example.chat.airport.kafka.message;

import com.example.chat.airport.plane.Plane;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaneIndexingMessage {

    private String planeId;
    private String flightId;
    private String airLine;
    private String airport;
    private String airportCode;
    private String scheduleDateTime;
    private String estimatedDateTime;
    private String gatenumber;
    private String terminalid;
    private String remark;
    private String searchDate;
    private String chkinrange;

    public static PlaneIndexingMessage from(Plane plane) {
        return PlaneIndexingMessage.builder()
                .planeId(String.valueOf(plane.getId()))
                .flightId(plane.getFlightId())
                .airLine(plane.getAirLine())
                .airport(plane.getAirport())
                .airportCode(plane.getAirportCode())
                .scheduleDateTime(plane.getScheduleDateTime())
                .estimatedDateTime(plane.getEstimatedDateTime())
                .gatenumber(plane.getGatenumber())
                .terminalid(plane.getTerminalid())
                .remark(plane.getRemark())
                .searchDate(plane.getSearchDate())
                .chkinrange(plane.getChkinrange())
                .build();
    }
}
