package com.example.chat.airport.transit.application;

import com.example.chat.airport.transit.ui.TransitTimeResDto;
import com.example.chat.airport.transit.domain.ArexTransitTime;
import com.example.chat.airport.transit.domain.ArexTransitTimeRepository;
import com.example.chat.airport.transit.domain.ParkingTransitTime;
import com.example.chat.airport.transit.domain.ParkingTransitTimeRepository;
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
    public List<TransitTimeResDto> getArexTransitTime() {
        List<ArexTransitTime> results = arexRepository.findAll();

        return results.stream()
                .map(TransitTimeResDto::fromArex)
                .collect(Collectors.toList());
    }

    // 주차장 소요시간 조회
    public List<TransitTimeResDto> getParkingTransitTime() {

        List<ParkingTransitTime> results = parkingRepository.findAll();

        return results.stream()
                .map(TransitTimeResDto::fromParking)
                .collect(Collectors.toList());
    }
}
