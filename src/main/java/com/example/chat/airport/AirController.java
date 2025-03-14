package com.example.chat.airport;

import com.example.chat.airport.entity.Departure;
import com.example.chat.airport.entity.Plane;
import com.example.chat.airport.repo.PlaneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class AirController {

    private final AirService airService;

    private final PlaneRepository planeRepository;
    // 인천 공항 출 입국 현황
    @GetMapping("/arrivals")
    public void getArrivalsData() {
        airService.getArrivalsData();
    }

    // 인천 공항 항공기 운항 현황 ( 도착 , 출발 )
    @GetMapping("/planes")
    public void getPlane() {
        airService.getPlane();
    }

    @GetMapping("/get/departures")
    public List<Departure> getDepartures() {
        return airService.getDepartures();
    }

    @GetMapping("/get/planes")
    public List<Plane> getRedisPlanes() {
        return airService.getAllPlanes();
    }

    @DeleteMapping("/delete/yesterday/planes")
    public void deleteYes() {
        airService.PlaneDelAndIst();
    }
}
