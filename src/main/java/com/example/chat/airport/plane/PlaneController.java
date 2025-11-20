package com.example.chat.airport.plane;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/airport")
@RestController
public class PlaneController {

    private final PlaneService planeService;

    // 인천공항 항공편 api 로딩
    @GetMapping("/api/planes")
    public void getPlane() {
        planeService.getPlaneData();
    }

    // 항공편 데이터 조회
    @GetMapping("/planes")
    public List<PlaneResDto> getRedisPlanes() {
        return planeService.getPlanes(null, PlaneResDto.class);
    }

    // 항공편 데이터 정리
    @DeleteMapping("/delete/cleanUpPlaneData")
    public void cleanUpPlaneData() {
        planeService.cleanUpPlaneData();
    }
}
