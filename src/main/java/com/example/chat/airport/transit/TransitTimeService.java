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
