package com.example.chat.airport.plane;

import com.example.chat.airport.ApiService;
import com.example.chat.airport.plane.dto.PlaneResDto;
import com.example.chat.common.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        for (int offset = -1; offset <= 2; offset++) {
            String date = LocalDate.now().plusDays(offset).format(DateUtils.BASIC_DATE);

            try {
                apiService.fetchAndSyncPlaneData(date);
                log.info("{}일({}) 항공편 수동 동기화 완료", offset, date);
            } catch (Exception e) {
                log.error("{}일({}) 항공편 수동 동기화 실패", offset, date, e);
            }
        }
    }

    // 모든 항공편 데이터 삭제
    @DeleteMapping("/planes/deleteAll")
    public void deleteAllPlanes() {
        planeService.deleteAll();
    }
}
