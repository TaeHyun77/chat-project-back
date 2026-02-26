package com.example.chat.airport;

import com.example.chat.airport.Departure.DepartureService;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ApiService {

    @Value("${data.api.key}") // 공공 데이터 API 키
    private String API_KEY;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    private final DepartureService departureService;
    private final PlaneService planeService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final List<String> departureSearchDates = List.of("0", "1");

    // 공항 출국장 혼잡도 데이터를 인천공항 API를 통해 받아옴
    public void getApiDeparture() {
        try {
            for (String searchDate : departureSearchDates) {

                // 출국장 현황 데이터 조회 API end_point
                String departureDataEndPoint = "https://apis.data.go.kr/B551177/passgrAnncmt/getPassgrAnncmt";

                URI uri = buildUri("departure", departureDataEndPoint, searchDate);

                // JSON 형태로 받아옴
                String apiDepartureData = restTemplate.getForObject(uri, String.class);
                JsonNode jsonDepartureData = parseAndValidateJson(apiDepartureData);

                departureService.upsertDepartureData(jsonDepartureData);
            }

        } catch (Exception e) {
            log.error("출국장 데이터 api 조회 예외 발생: {}", e.getMessage());
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_DEPARTURE_DATA);
        }
    }

    // 공항 항공편 현황 데이터를 인천공항 API를 통해 받아옴
    public void getApiPlane() {

        // 항공편 현황 데이터 조회 API end_point
        String planeDataEndPoint = "https://apis.data.go.kr/B551177/StatusOfPassengerFlightsDeOdp/getPassengerDeparturesDeOdp";

        LocalDateTime today = LocalDateTime.now();

        List<String> searchDates = List.of(
                today.minusDays(1).format(formatter), // 어제
                today.format(formatter),              // 오늘
                today.plusDays(1).format(formatter),  // 내일
                today.plusDays(2).format(formatter)   // 모레
        );

        try {
            for (String searchDate : searchDates) {
                URI uri = buildUri("plane", planeDataEndPoint, searchDate);

                String apiPlaneData = restTemplate.getForObject(uri, String.class);
                JsonNode jsonPlaneData = parseAndValidateJson(apiPlaneData);

                planeService.upsertPlaneData(jsonPlaneData, searchDate);
            }

        } catch (Exception e) {
            log.error("항공편 데이터 저장 중 예외 발생: {}", e.getMessage());
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_PLANE_DATA);
        }
    }

    // OpenAPI 호출에 필요한 요청 URI를 반환
    private URI buildUri(String type, String endPoint, String searchDate) throws URISyntaxException {

        String date = (type.equals("departure")) ? "&selectdate=" : "&searchday=";

        // OpenAPI 요청 시 쿼리 파라미터를 URL 인코딩해야함
        String url = endPoint + "?"
                + "serviceKey=" + URLEncoder.encode(API_KEY, StandardCharsets.UTF_8)
                + date + URLEncoder.encode(searchDate, StandardCharsets.UTF_8)
                + "&pageNo=" + URLEncoder.encode("1", StandardCharsets.UTF_8)
                + "&numOfRows=" + URLEncoder.encode("9999", StandardCharsets.UTF_8)
                + "&type=" + URLEncoder.encode("json", StandardCharsets.UTF_8);

        return new URI(url);
    }

    /*
     * OpenAPI에서 가져온 JSON 데이터의 유효성 판단
     * */
    public JsonNode parseAndValidateJson(String jsonData) {
        if (jsonData == null || jsonData.isEmpty() || jsonData.startsWith("<")) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_CHANGE_JSON_DATE);
        }

        try {
            return objectMapper.readTree(jsonData).path("response").path("body").path("items");
        } catch (JsonProcessingException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_PARSE_JSON);
        }
    }
}
