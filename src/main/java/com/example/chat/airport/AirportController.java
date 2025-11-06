package com.example.chat.airport;

import com.example.chat.airport.Departure.DepartureResDto;
import com.example.chat.airport.plane.PlaneResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/airport")
@RestController
public class AirportController {

    private final AirportService airportService;

    // 인천공항 항공편 api 로딩
    @GetMapping("/api/planes")
    public void getPlane() {
        airportService.getPlane();
    }

    // 출국장 혼잡도 데이터 조회
    @GetMapping("/get/departures")
    public List<DepartureResDto> getDepartures() {
        return airportService.getDepartures();
    }

    // 항공편 데이터 조회
    @GetMapping("/get/planes")
    public List<PlaneResDto> getRedisPlanes() {
        return airportService.getAllPlanes();
    }
}
