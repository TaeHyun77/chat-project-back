package com.example.chat.airport.search.dto;

import com.example.chat.airport.search.FlightDocument;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FlightSearchResDto {

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

    public static FlightSearchResDto from(FlightDocument doc) {
        return FlightSearchResDto.builder()
                .flightId(doc.getFlightId())
                .airLine(doc.getAirLine())
                .airport(doc.getAirport())
                .airportCode(doc.getAirportCode())
                .scheduleDateTime(doc.getScheduleDateTime())
                .estimatedDateTime(doc.getEstimatedDateTime())
                .gatenumber(doc.getGatenumber())
                .terminalid(doc.getTerminalid())
                .remark(doc.getRemark())
                .searchDate(doc.getSearchDate())
                .build();
    }
}
