package com.example.chat.airport.plane;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
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

    @GetMapping("/slice/planes")
    public Slice<PlaneResDto> getPlanes(
            @RequestParam String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size
    ) {
        return planeService.getSlicePlanesBySearchDate(date, page, size);

    }

    // 항공편 데이터 정리
    @DeleteMapping("/delete/cleanUpPlaneData")
    public void cleanUpPlaneData() {
        planeService.cleanUpPlaneData();
    }
}
