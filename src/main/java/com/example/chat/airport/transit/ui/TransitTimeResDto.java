package com.example.chat.airport.transit.ui;

import com.example.chat.airport.transit.domain.ArexTransitTime;
import com.example.chat.airport.transit.domain.ParkingTransitTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransitTimeResDto {

    private String terminal;
    private String from;
    private String checkInCounter;
    private int travelSeconds;
    private int travelMinutes;

    public static TransitTimeResDto fromArex(ArexTransitTime t) {
        return TransitTimeResDto.builder()
                .terminal(t.getTerminal())
                .from(t.getStationName())
                .checkInCounter(t.getCheckInCounter())
                .travelSeconds(t.getTravelSeconds())
                .travelMinutes(t.getTravelSeconds() / 60)
                .build();
    }

    public static TransitTimeResDto fromParking(ParkingTransitTime t) {
        return TransitTimeResDto.builder()
                .terminal(t.getTerminal())
                .from(t.getParkingName() + " " + t.getZone())
                .checkInCounter(t.getCheckInCounter())
                .travelSeconds(t.getTravelSeconds())
                .travelMinutes(t.getTravelSeconds() / 60)
                .build();
    }
}
