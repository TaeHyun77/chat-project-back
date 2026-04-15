package com.example.chat.airport.parking.dto;

import com.example.chat.airport.parking.Parking;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParkingResDto {
    private String floor;
    private int parking;
    private int parkingarea;
    private int availableRate;
    private String datetm;

    public static ParkingResDto from(Parking parking) {
        return ParkingResDto.builder()
                .floor(parking.getFloor())
                .parking(parking.getParking())
                .parkingarea(parking.getParkingarea())
                .availableRate(parking.getAvailableRate())
                .datetm(parking.getDatetm())
                .build();
    }
}
