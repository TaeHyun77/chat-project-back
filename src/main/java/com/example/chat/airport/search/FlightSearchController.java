package com.example.chat.airport.search;

import com.example.chat.airport.plane.PlaneService;
import com.example.chat.airport.search.dto.FlightSearchResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/flights")
public class FlightSearchController {

    private final FlightSearchService flightSearchService;
    private final PlaneService planeService;

    // 복합 조건 검색 ( fuzzy 포함 )
    // subscribable이 true이면 구독용 항공편 목록 , false이면 전체 항공편 목록
    @GetMapping("/search")
    public ResponseEntity<List<FlightSearchResDto>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String terminal,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String airline
    ) {

        return ResponseEntity.ok(flightSearchService.search(q, terminal, date, airline));
    }

    // 자동완성
    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> autocomplete(
            @RequestParam String q,
            @RequestParam(required = false) String date
    ) {
        return ResponseEntity.ok(flightSearchService.autocomplete(q, date));
    }

    // ES 재인덱싱
    @PostMapping("/reindex")
    public ResponseEntity<Map<String, Object>> reindex() {
        int count = planeService.reindexAll();
        return ResponseEntity.ok(Map.of("message", "재인덱싱 요청 완료", "count", count));
    }
}
