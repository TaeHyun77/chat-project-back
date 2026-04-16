package com.example.chat.airport.transit;

import com.example.chat.airport.transit.dto.TransitTimeResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/airport/transit")
public class TransitTimeController {

    private final TransitTimeService transitTimeService;

    // 공항철도 소요시간 목록 조회
    @GetMapping("/arex")
    public List<TransitTimeResDto> getArexTransitTime() {
        return transitTimeService.getArexTransitTime();
    }

    // 주차장 소요시간 목록 조회
    @GetMapping("/parking")
    public List<TransitTimeResDto> getParkingTransitTime() {
        return transitTimeService.getParkingTransitTime();
    }
}
