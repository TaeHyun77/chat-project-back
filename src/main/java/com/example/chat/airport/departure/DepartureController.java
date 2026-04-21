package com.example.chat.airport.departure;

import com.example.chat.airport.departure.dto.DepartureResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequestMapping("/airport")
@RequiredArgsConstructor
@RestController
public class DepartureController {

    private final DepartureService departureService;

    // 출국장 혼잡도 데이터 목록 조회
    @GetMapping("/departures")
    public List<DepartureResDto> getDepartures() {
        return departureService.getDepartures();
    }
}