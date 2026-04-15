package com.example.chat.airport.transit;

import com.example.chat.airport.transit.dto.TransitTimeResDto;
import com.example.chat.airport.transit.arexTime.ArexTransitTime;
import com.example.chat.airport.transit.arexTime.ArexTransitTimeRepository;
import com.example.chat.airport.transit.parkingTime.ParkingTransitTime;
import com.example.chat.airport.transit.parkingTime.ParkingTransitTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TransitTimeService {

    private final ArexTransitTimeRepository arexRepository;
    private final ParkingTransitTimeRepository parkingRepository;

    // 공항철도 소요시간 조회
    public List<TransitTimeResDto> getArexTransitTime(String terminal, String counter) {
        List<ArexTransitTime> results = (counter != null && !counter.isBlank())
                ? arexRepository.findByTerminalAndCheckInCounter(terminal, counter)
                : arexRepository.findAll().stream()
                    .filter(t -> t.getTerminal().equalsIgnoreCase(terminal))
                    .toList();

        return results.stream()
                .map(TransitTimeResDto::fromArex)
                .collect(Collectors.toList());
    }

    // 주차장 소요시간 조회
    public List<TransitTimeResDto> getParkingTransitTime(
            String terminal, String parking, String zone, String counter) {

        List<ParkingTransitTime> results;

        if (parking != null && !parking.isBlank() && zone != null && !zone.isBlank()
                && counter != null && !counter.isBlank()) {
            results = parkingRepository.findByTerminalAndParkingNameAndZoneAndCheckInCounter(
                    terminal, parking, zone, counter);
        } else {
            results = parkingRepository.findByTerminalAndCheckInCounter(terminal,
                    counter != null ? counter : "");
        }

        return results.stream()
                .map(TransitTimeResDto::fromParking)
                .collect(Collectors.toList());
    }
}
