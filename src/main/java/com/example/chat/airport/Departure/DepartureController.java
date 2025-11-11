package com.example.chat.airport.Departure;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class DepartureController {

    private final DepartureService departureService;

    // 출국장 혼잡도 데이터 조회
    @GetMapping("/departures")
    public List<DepartureResDto> getDepartures() {
        return departureService.getDepartures();
    }
}
