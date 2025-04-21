package com.example.chat.airport;

import com.example.chat.airport.repository.PlaneRepository;
import com.example.chat.airport.resDto.DepartureResDto;
import com.example.chat.airport.resDto.PlaneResDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AirController {

    private final AirService airService;

    // 인천 공항 출입국 현황
    @GetMapping("/arrivals")
    public void getArrivalsData() {
        airService.getArrivalsData();
    }

    // 인천 공항 항공기 운항 현황 ( 도착 , 출발 )
    @GetMapping("/planes")
    public void getPlane() {
        airService.getPlane();
    }

    // 출입국 데이터
    @GetMapping("/get/departures")
    public List<DepartureResDto> getDepartures() {
        return airService.getDepartures();
    }

    // 항공편 데이터
    @GetMapping("/get/planes")
    public List<PlaneResDto> getRedisPlanes() {
        return airService.getAllPlanes();
    }

    @DeleteMapping("/delete/yesterday/planes")
    public void deleteYesterdayPlanes() {

        airService.PlaneDelAndIst();
        
    }

    // 페이징 테스트
    @GetMapping("/test/page")
    public ResponseEntity<List<DepartureResDto>> testPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "0") int size) {

        Page<DepartureResDto> departurePage = airService.testPage(page, size);

        return new ResponseEntity<>(departurePage.getContent(), HttpStatus.OK);
    }
}
