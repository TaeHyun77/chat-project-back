package com.example.chat.airport.plane;

import com.example.chat.airport.ApiService;
import com.example.chat.airport.plane.dto.PlaneResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/airport")
@RestController
public class PlaneController {

    private final PlaneService planeService;
    private final ApiService apiService;

    @GetMapping("/slice/planes")
    public Slice<PlaneResDto> getPlanes(
            @RequestParam String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size
    ) {
        return planeService.getSlicePlanesBySearchDate(date, page, size);
    }

    // 어제~모레 항공편 동기화
    @PostMapping("/planes/sync")
    public void syncAllPlanes() {
        apiService.syncPlaneDataForDays();
    }

    // 모든 항공편 데이터 삭제
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/planes/deleteAll")
    public void deleteAllPlanes() {
        planeService.deleteAll();
    }
}
