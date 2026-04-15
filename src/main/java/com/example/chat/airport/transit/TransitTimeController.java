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

    // 공항철도 소요시간 조회
    @GetMapping("/arex")
    public ResponseEntity<List<TransitTimeResDto>> getArexTransitTime(
            @RequestParam String terminal,
            @RequestParam(required = false) String counter) {
        return ResponseEntity.ok(transitTimeService.getArexTransitTime(terminal, counter));
    }

    // 주차장 소요시간 조회
    @GetMapping("/parking")
    public ResponseEntity<List<TransitTimeResDto>> getParkingTransitTime(
            @RequestParam String terminal,
            @RequestParam(required = false) String parking,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String counter) {
        return ResponseEntity.ok(transitTimeService.getParkingTransitTime(terminal, parking, zone, counter));
    }
}
