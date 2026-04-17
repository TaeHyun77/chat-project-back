package com.example.chat.airport;

import com.example.chat.airport.departure.DepartureService;
import com.example.chat.airport.parking.ParkingService;
import com.example.chat.airport.plane.PlaneService;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ApiService {

    @Value("${data.api.key}") // 공공 데이터 API 키
    private String apiKey;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private final DepartureService departureService;
    private final PlaneService planeService;
    private final ParkingService parkingService;

    private static final String DEPARTURE_ENDPOINT =
            "https://apis.data.go.kr/B551177/passgrAnncmt/getPassgrAnncmt";

    private static final String PLANE_ENDPOINT =
            "https://apis.data.go.kr/B551177/StatusOfPassengerFlightsDeOdp/getPassengerDeparturesDeOdp";

    private static final String PARKING_ENDPOINT =
            "http://apis.data.go.kr/B551177/StatusOfParking/getTrackingParking";

    private final List<String> departureSearchDates = List.of("0", "1");

    // 단일 날짜 항공편 동기화
    public void fetchAndSyncPlaneData(String searchDate) {
        URI uri = buildAirportUri("plane", PLANE_ENDPOINT, searchDate);
        String response = restTemplate.getForObject(uri, String.class);
        JsonNode json = parseAndValidateJson(response);

        planeService.upsertPlaneData(json, searchDate);
    }

    // 공항 출국장 혼잡도 데이터를 인천공항 API를 통해 받아옴
    public void getApiDeparture() {
        for (String searchDate : departureSearchDates) {
            URI uri = buildAirportUri("departure", DEPARTURE_ENDPOINT, searchDate);

            String apiDepartureData = restTemplate.getForObject(uri, String.class);
            JsonNode jsonDepartureData = parseAndValidateJson(apiDepartureData);

            departureService.upsertDepartureData(jsonDepartureData);
        }
    }

    // 주차장 실시간 현황 데이터를 공공 API를 통해 받아옴
    public void getApiParking() {
        URI uri = UriComponentsBuilder.fromUriString(PARKING_ENDPOINT)
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 9999)
                .queryParam("type", "json")
                .build(true)
                .toUri();

        String apiParkingData = restTemplate.getForObject(uri, String.class);
        JsonNode jsonParkingData = parseAndValidateJson(apiParkingData);
        parkingService.upsertParkingData(jsonParkingData);
    }

    // OpenAPI 호출에 필요한 요청 URI를 반환
    private URI buildAirportUri(String type, String endPoint, String searchDate) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(endPoint)
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 9999)
                .queryParam("type", "json");

        if ("departure".equals(type)) {
            builder.queryParam("selectdate", searchDate);
        } else {
            builder.queryParam("searchday", searchDate);
        }

        return builder
                .build(true)
                .toUri();
    }

    // OpenAPI에서 가져온 JSON 데이터의 유효성 판단
    public JsonNode parseAndValidateJson(String jsonData) {
        if (jsonData == null || jsonData.isEmpty()) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_CHANGE_JSON_DATE);
        }

        if (jsonData.startsWith("<")) {
            log.error("공공 API가 XML 또는 에러 응답을 반환했습니다: {}", jsonData);
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_CHANGE_JSON_DATE);
        }

        try {
            return objectMapper.readTree(jsonData)
                    .path("response")
                    .path("body")
                    .path("items");
        } catch (JsonProcessingException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_PARSE_JSON);
        }
    }
}