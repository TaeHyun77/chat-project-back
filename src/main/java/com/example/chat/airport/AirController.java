package com.example.chat.airport;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AirController {

    private final AirService airService;


    // 인천 공항 출 입국 현황
    @GetMapping("/arrivals")
    public void getArrivalsData() {
        airService.getArrivalsData();
    }

    // 인천 공항 항공기 운항 현황 ( 도착 , 출발 )
    @GetMapping("/plane")
    public void getPlane() {
        airService.getPlane();
    }
}
