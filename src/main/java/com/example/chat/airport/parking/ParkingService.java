package com.example.chat.airport.parking;

import com.example.chat.airport.parking.dto.ParkingResDto;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParkingService {

    private final ParkingRepository parkingRepository;

    @Transactional
    public void upsertParkingData(JsonNode items) {
        for (JsonNode item : items) {
            String floor = item.path("floor").asText();
            int parking = item.path("parking").asInt();
            int parkingarea = item.path("parkingarea").asInt();
            String datetm = item.path("datetm").asText();

            parkingRepository.findByFloor(floor)
                    .ifPresentOrElse(
                            exists -> exists.updateParking(parking, parkingarea, datetm),
                            () -> parkingRepository.save(Parking.builder()
                                    .floor(floor)
                                    .parking(parking)
                                    .parkingarea(parkingarea)
                                    .datetm(datetm)
                                    .build())
                    );
        }
    }

    public List<ParkingResDto> getParkingStatus() {
        return parkingRepository.findAll().stream()
                .map(ParkingResDto::from)
                .collect(Collectors.toList());
    }
}
