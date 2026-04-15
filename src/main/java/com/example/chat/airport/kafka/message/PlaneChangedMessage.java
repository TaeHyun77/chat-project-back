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
public class PlaneChangedMessage {

    private String flightId;
    private String airLine;
    private String airport;
    private String scheduleDateTime;
    private String terminalid;
    private String searchDate;

    // 변경 전 값
    private String prevRemark;
    private String prevEstimatedDateTime;
    private String prevGatenumber;
    private String prevTerminalId;
    private String prevChkinrange;

    // 변경 후 값
    private String newRemark;
    private String newEstimatedDateTime;
    private String newGatenumber;
    private String newTerminalId;
    private String newChkinrange;

    public static PlaneChangedMessage from(Plane plane,
                                           String newRemark,
                                           String newEstimatedDateTime,
                                           String newGatenumber,
                                           String newTerminalId,
                                           String newChkinrange) {
        return PlaneChangedMessage.builder()
                .flightId(plane.getFlightId())
                .airLine(plane.getAirLine())
                .airport(plane.getAirport())
                .scheduleDateTime(plane.getScheduleDateTime())
                .terminalid(plane.getTerminalid())
                .searchDate(plane.getSearchDate())
                .prevRemark(plane.getRemark())
                .prevEstimatedDateTime(plane.getEstimatedDateTime())
                .prevGatenumber(plane.getGatenumber())
                .prevTerminalId(plane.getTerminalid())
                .prevChkinrange(plane.getChkinrange())
                .newRemark(newRemark)
                .newEstimatedDateTime(newEstimatedDateTime)
                .newGatenumber(newGatenumber)
                .newTerminalId(newTerminalId)
                .newChkinrange(newChkinrange)
                .build();
    }
}
