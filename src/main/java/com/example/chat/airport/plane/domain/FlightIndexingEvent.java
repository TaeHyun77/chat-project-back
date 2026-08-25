package com.example.chat.airport.plane.domain;

import com.example.chat.airport.plane.domain.Plane;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 항공편 신규/변경 시 Elasticsearch 인덱싱을 요청하는 도메인 이벤트.
 * 커밋 이후 리스너에서 ES에 반영된다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightIndexingEvent {
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

    public static FlightIndexingEvent from(Plane plane) {
        return FlightIndexingEvent.builder()
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
