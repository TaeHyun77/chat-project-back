package com.example.chat.airport.Departure;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DepartureService {

    @Value("${data.api.key}") // 공공 데이터 API 키
    private String API_KEY;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final DepartureRepository departureRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /*
     * 공항 출국장 현황 데이터 조회 및 갱신
     *
     * Api 규칙 : searchDate = 0 (오늘), 1(내일), 2(모레) ...
     * */
    @Transactional
    public void getDepartureData() {

        List<String> searchDates = List.of("0", "1"); // 오늘, 내일

        try {
            for (String searchDate : searchDates) {

                // 출국장 현황 데이터 조회 API end_point
                String departureDataEndPoint = "https://apis.data.go.kr/B551177/passgrAnncmt/getPassgrAnncmt";

                URI uri = departureBuildUri(departureDataEndPoint, searchDate);

                // JSON 형태로 받아옴
                String departureData = restTemplate.getForObject(uri, String.class);

                upsertDepartureData(departureData);
            }

        } catch (Exception e) {
            log.error("출국장 데이터 api 조회 예외 발생: {}", e.getMessage());

            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_DEPARTURE_DATA);
        }
    }

    /*
     * 공항 출국장 현황 데이터를 DB에 갱신
     * */
    @Transactional
    private void upsertDepartureData(String departureJsonData) {
        try {

            JsonNode items = checkValidationJson(departureJsonData);

            for (JsonNode item : items) {

                String date = item.path("adate").asText();
                String timeZone = item.path("atime").asText();

                if (date.equals("합계")) continue;

                DepartureDto dto = DepartureDto.builder()
                        .date(item.path("adate").asText())
                        .timeZone(item.path("atime").asText())
                        .t1Depart1(item.path("t1dg1").asLong())
                        .t1Depart2(item.path("t1dg2").asLong())
                        .t1Depart3(item.path("t1dg3").asLong())
                        .t1Depart4(item.path("t1dg4").asLong())
                        .t1Depart5(item.path("t1dg5").asLong())
                        .t1Depart6(item.path("t1dg6").asLong())
                        .t2Depart1(item.path("t2dg1").asLong())
                        .t2Depart2(item.path("t2dg2").asLong())
                        .build();

                departureRepository.findByDateAndTimeZone(date, timeZone)
                        .ifPresentOrElse(
                                exists -> {
                                    exists.updateDeparture(dto.toDepart());

                                    departureRepository.save(exists);
                                },
                                () -> departureRepository.save(dto.toDepart())
                        );
            }
        } catch (Exception e) {
            log.error("출국장 데이터 저장 중 예외 발생: {}", e.getMessage());

            throw e;
        }
    }

    // 모든 출국장 데이터 조회
    public List<DepartureResDto> getDepartures() {

        List<Departure> departures = departureRepository.findAll();

        return departures.stream()
                .map(d -> DepartureResDto.builder()
                        .date(d.getDate())
                        .timeZone(d.getTimeZone())
                        .t1Depart1(d.getT1Depart1())
                        .t1Depart2(d.getT1Depart2())
                        .t1Depart3(d.getT1Depart3())
                        .t1Depart4(d.getT1Depart4())
                        .t1Depart5(d.getT1Depart5())
                        .t1Depart6(d.getT1Depart6())
                        .t2Depart1(d.getT2Depart1())
                        .t2Depart2(d.getT2Depart2())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void cleanUpDepartureData() {
        String yesterday = LocalDate.now().minusDays(1).format(formatter);

        departureRepository.deleteByDate(yesterday);

    }

    public JsonNode checkValidationJson(String jsonData) {
        if (jsonData == null || jsonData.isEmpty() || jsonData.startsWith("<")) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_CHANGE_JSON_DATE);
        }

        try {
            return objectMapper.readTree(jsonData).path("response").path("body").path("items");
        } catch (JsonProcessingException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_PARSE_JSON);
        }
    }

    /*
     * OpenAPI 호출에 필요한 요청 URI를 반환
     * */
    private URI departureBuildUri(String endPoint, String searchDate) throws URISyntaxException {

        // OpenAPI 요청 시 쿼리 파라미터를 URL 인코딩해야함
        String url = endPoint + "?"
                + "serviceKey=" + URLEncoder.encode(API_KEY, StandardCharsets.UTF_8)
                + "&selectdate=" + URLEncoder.encode(searchDate, StandardCharsets.UTF_8)
                + "&pageNo=" + URLEncoder.encode("1", StandardCharsets.UTF_8)
                + "&numOfRows=" + URLEncoder.encode("9999", StandardCharsets.UTF_8)
                + "&type=" + URLEncoder.encode("json", StandardCharsets.UTF_8);

        return new URI(url);
    }



}
