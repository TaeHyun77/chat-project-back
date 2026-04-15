package com.example.chat.airport.search;

import com.example.chat.airport.search.dto.FlightSearchResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/flights")
public class FlightSearchController {

    private final FlightSearchService flightSearchService;

    // 복합 조건 검색 (fuzzy 포함)
    @GetMapping("/search")
    public ResponseEntity<List<FlightSearchResDto>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String terminal,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String airline) {
        return ResponseEntity.ok(flightSearchService.search(q, terminal, date, airline));
    }

    // 자동완성
    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> autocomplete(@RequestParam String q) {
        return ResponseEntity.ok(flightSearchService.autocomplete(q));
    }
}
