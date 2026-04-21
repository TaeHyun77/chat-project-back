package com.example.chat.airport.parking;

import com.example.chat.airport.parking.dto.ParkingResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/airport")
public class ParkingController {

    private final ParkingService parkingService;

    // 주차장 이용 수치 데이터 목록 조회
    @GetMapping("/parking")
    public List<ParkingResDto> getParkingStatus() {
        return parkingService.getParkingStatus();
    }
}
