package com.example.chat.airport.plane;

import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlaneService {

    @Value("${data.api.key}") // 공공 데이터 API 키
    private String API_KEY;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();
    private final PlaneRepository planeRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /*
    * 공항 항공편 현황 데이터를 갱신 및 저장
    *
    * 바뀔 수 있는 값 : estimatedDatetime, flightId, gateNumber, ** remark, terminalId
    * */
    @Transactional
    public void getPlaneData() {

        LocalDateTime today = LocalDateTime.now();

        List<String> searchDates = List.of(
                today.minusDays(1).format(formatter), // 어제
                today.format(formatter),              // 오늘
                today.plusDays(1).format(formatter),  // 내일
                today.plusDays(2).format(formatter)   // 모레
        );
        
        try {
            for (String searchDate : searchDates) {

                // 항공편 현황 데이터 조회 API end_point
                String planeDataEndPoint = "https://apis.data.go.kr/B551177/StatusOfPassengerFlightsDeOdp/getPassengerDeparturesDeOdp";
                URI uri = planeBuildUri(planeDataEndPoint, searchDate);

                String jsonPlaneData = restTemplate.getForObject(uri, String.class);

                upsertPlaneData(jsonPlaneData, searchDate);
            }

        } catch (Exception e) {
            log.error("항공편 데이터 저장 중 예외 발생: {}", e.getMessage());
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_PLANE_DATA);
        }
    }

    /*
    * API를 통해 조회한 공항 항공편 현황 데이터를 DB에 저장 및 갱신
    * */
    @Transactional
    private void upsertPlaneData(String jsonPlaneData, String searchDate) {
        try {
            JsonNode items = checkValidationJson(jsonPlaneData);

            List<Plane> existPlaneDb = getPlanesBySearchDate(searchDate);

            // 기존에 존재하는 항공편 데이터를 Map에 key-value 형태로 저장
            // key : FlightId + scheduleDateTime
            Map<String, Plane> existPlaneDbMap = existPlaneDb.stream()
                    .collect(Collectors.toMap(p -> p.getFlightId() + "_" + p.getScheduleDateTime(), p -> p));

            // DB에 저장할 항공편 데이터를 담는 리스트
            List<Plane> toSave = toSavePlane(items, existPlaneDbMap, searchDate);

            planeRepository.saveAll(toSave);

        } catch (Exception e) {
            log.error("항공편 데이터 저장 예외 발생: {}", e.getMessage());
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_PLANE_DATA);
        }
    }

    /*
    * API를 통해 조회한 공항 항공편 현황 데이터를 실제로 처리하는 로직
    *
    * 어제 항공편 데이터를 포함하는 이유는 지연이나 결항 등의 사유로 일정이 다음 날로 변경될 수 있기 때문이며, 이 경우 새로운 데이터로 저장하지 않고 기존 정보의 갱신 여부만 확인
     * */
    private List<Plane> toSavePlane(JsonNode items, Map<String, Plane> existPlaneDbMap, String searchDate) {

        String yesterday = LocalDate.now().minusDays(1).format(formatter);

        // DB에 저장할 항공편 데이터를 담는 리스트
        List<Plane> toSave = new ArrayList<>();

        // JSON 데이터에서 같은 데이터가 여러 개 들어오는 경우가 있기에 중복을 제거하기 위함
        Set<String> checkDuplication = new HashSet<>();

        // API에서 가져온 데이터를 순회하여 처리
        for (JsonNode item : items) {
            String codeshare = item.path("codeshare").asText();

            if (!codeshare.equals("Master")) continue;

            PlaneDto dto = PlaneDto.builder()
                    .searchDate(searchDate)
                    .flightId(item.path("flightId").asText())
                    .airLine(item.path("airline").asText())
                    .airport(item.path("airport").asText())
                    .airportCode(item.path("airportCode").asText())
                    .scheduleDateTime(item.path("scheduleDateTime").asText())
                    .estimatedDateTime(item.path("estimatedDateTime").asText())
                    .gatenumber(item.path("gatenumber").asText())
                    .terminalid(item.path("terminalid").asText())
                    .remark(item.path("remark").asText())
                    .codeShare(item.path("codeshare").asText())
                    .chkinrange(item.path("chkinrange").asText())
                    .build();

            String key = dto.getFlightId() + "_" + dto.getScheduleDateTime();

            // 중복되는 데이터는 거름
            if (checkDuplication.contains(key)) {
                continue;
            } else {
                checkDuplication.add(key);
            }

            // DB에 존재하는지 여부
            Plane existingPlane = existPlaneDbMap.get(key);

            // DB에 이미 존재한다면, 데이터가 수정된 경우에만 다시 저장
            // 이때, 따로 save 해주지 않아도 되기에 toSave에는 add 하지 않았음
            if (existingPlane != null) {
                if (planeDataChangeCheck(existingPlane, dto.getRemark(), dto.getEstimatedDateTime(), dto.getGatenumber(), dto.getTerminalid(), dto.getChkinrange())) {

                    // 더티 체킹
                    existingPlane.updatePlane(dto.getRemark(), dto.getEstimatedDateTime(), dto.getGatenumber(), dto.getTerminalid(), dto.getChkinrange());
                }

            // DB에 존재하지 않는 경우
            } else {

                if (searchDate.equals(yesterday)) continue;

                Plane newPlane = dto.toPlane();
                toSave.add(newPlane);
            }
        }

        return toSave;
    }

    // 항공편 데이터 변경 여부 - 하나라도 변경되면 true 반환
    private boolean planeDataChangeCheck(Plane existingPlane, String remark , String estimatedDatetime, String gateNumber, String terminalId, String checkinrange) {

        return !Objects.equals(existingPlane.getRemark(), remark)
                || !Objects.equals(existingPlane.getEstimatedDateTime(), estimatedDatetime)
                || !Objects.equals(existingPlane.getGatenumber(), gateNumber)
                || !Objects.equals(existingPlane.getTerminalid(), terminalId)
                || !Objects.equals(existingPlane.getChkinrange(), checkinrange);
    }

    public List<Plane> getPlanesBySearchDate(String searchDate) {

        return planeRepository.findBySearchDate(searchDate);
    }

    public Slice<PlaneResDto> getSlicePlanesBySearchDate(String date, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("scheduleDateTime").ascending());

        Slice<Plane> slicePlane = planeRepository.findBySearchDate(date, pageable);

        return slicePlane.map(PlaneResDto::from);
    }

    /*
    * OpenAPI 호출에 필요한 요청 URI를 반환
    * */
    private URI planeBuildUri(String endPoint, String searchDate) throws URISyntaxException {

        // OpenAPI 요청 시 쿼리 파라미터를 URL 인코딩해야함
        String url = endPoint + "?"
                + "serviceKey=" + URLEncoder.encode(API_KEY, StandardCharsets.UTF_8)
                + "&searchday=" + URLEncoder.encode(searchDate, StandardCharsets.UTF_8)
                + "&pageNo=" + URLEncoder.encode("1", StandardCharsets.UTF_8)
                + "&numOfRows=" + URLEncoder.encode("9999", StandardCharsets.UTF_8)
                + "&type=" + URLEncoder.encode("json", StandardCharsets.UTF_8);

        return new URI(url);
    }

    /*
    * OpenAPI에서 가져온 JSON 데이터의 유효성 판단
    * */
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
    * 스케줄러를 통해 매 자정에 실행하는 항공편 데이터 정리 메서드
    * 오늘/내일/모레 데이터가 아니고 출발 상태인 데이터만 삭제
    * */
    @Transactional
    public void cleanUpPlaneData() {

        LocalDate today = LocalDate.now();

        // 오늘, 내일, 모레
        String todayStr = today.format(formatter);
        String tomorrowStr = today.plusDays(1).format(formatter);
        String dayAfterTomorrowStr = today.plusDays(2).format(formatter);

        List<Plane> planes = planeRepository.findAll();

        // 오늘/내일/모레 데이터가 아니고 출발 상태인 데이터만 삭제
        planes.stream()
                .filter(p -> "출발".equals(p.getRemark()))
                .filter(p -> {
                    String searchDate = p.getSearchDate();

                    return !searchDate.equals(todayStr) && !searchDate.equals(tomorrowStr) && !searchDate.equals(dayAfterTomorrowStr);
                })
                .forEach(planeRepository::delete);
    }
}