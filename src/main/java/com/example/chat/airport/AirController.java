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

    // 인천공항 항공편 현황
    @GetMapping("/planes")
    public void getPlane() {
        airService.getPlane();
    }

    // 출국장 혼잠도 데이터 조회
    @GetMapping("/get/departures")
    public List<DepartureResDto> getDepartures() {
        return airService.getDepartures();
    }

    // 항공편 데이터 조회
    @GetMapping("/get/planes")
    public List<PlaneResDto> getRedisPlanes() {
        return airService.getAllPlanes();
    }

    /*// 페이징 테스트
    @GetMapping("/test/page")
    public ResponseEntity<List<DepartureResDto>> testPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "0") int size) {

        Page<DepartureResDto> departurePage = airService.testPage(page, size);

        return new ResponseEntity<>(departurePage.getContent(), HttpStatus.OK);
    }*/
}
