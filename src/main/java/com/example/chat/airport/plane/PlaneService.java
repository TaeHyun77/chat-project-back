package com.example.chat.airport.plane;

import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlaneService {

    @Value("${data.api.key}") // 공공 데이터 API 키
    private String API_KEY;

    @Value("${redis.plane.hash.key}")
    private String REDIS_PLANE_HASH_KEY;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    private final RedisTemplate<String, Object> redisTemplate;
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

            // 기존에 존재하는 항공편 데이터 DB에 존재하면 DB에서, Redis에 존재하면 Redis에서 해당 searchDate의 데이터를 가져옴
            List<Plane> existPlaneDb = getPlanes(searchDate, Plane.class);

            // 기존에 존재하는 항공편 데이터를 Map에 key-value 형태로 저장
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
            if (checkDuplication.contains(key)) continue;
            checkDuplication.add(key);

            Plane existingPlane = existPlaneDbMap.get(key);

            // DB에 이미 해당 데이터가 존재한다면, 정보가 갱신된 경우에만 수정하여 다시 저장
            // 이때 따로 save 해주지 않아도 되기에 toSave에는 add 하지 않았음
            if (existingPlane != null) {
                if (planeDataChangeCheck(existingPlane, dto.getRemark(), dto.getEstimatedDateTime(), dto.getGatenumber(), dto.getTerminalid(), dto.getChkinrange())) {

                    // 더티 체킹
                    existingPlane.updatePlane(dto.getRemark(), dto.getEstimatedDateTime(), dto.getGatenumber(), dto.getTerminalid(), dto.getChkinrange());

                    // redis에도 변경사항 갱신
                    redisTemplate.opsForHash().put(REDIS_PLANE_HASH_KEY, key, existingPlane);
                }
            // DB에 해당 데이터가 존재하지 않는 경우 저장
            } else {

                if (searchDate.equals(yesterday)) continue;

                Plane newPlane = dto.toPlane();
                toSave.add(newPlane);

                // redis에 추가
                redisTemplate.opsForHash().put(REDIS_PLANE_HASH_KEY, key, newPlane);
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

    /*
    * 전체 항공편 데이터 목록 조회
    *
    * 1. redis에 존재하면 redis에서 조회
    * 2. redis에서 조회할 수 없다면 DB에서 조회 후 redis에 캐싱
    *
    * searchDate가 null이면 전체 데이터 조회, 값이 있다면 특정 searchDate의 데이터 조회
    * */
    public <T> List<T> getPlanes(String searchDate, Class<T> clazz) {
        List<T> redisPlanes = scanRedis(searchDate, clazz);

        if (!redisPlanes.isEmpty()) {
            log.info("redis에서 plane 데이터 조회");

            return redisPlanes;
        }

        // DB 조회

        // searchDate가 null 이면 전체 조회
        if (searchDate == null) {
            return getAllPlanes(clazz);
        }

        // 특정 searchDate의 데이터만 조회
        return getPlanesBySearchDate(clazz, searchDate);
    }

    /*
    * searchDate가 null이면 전체 데이터 조회, 값이 있다면 특정 searchDate의 데이터 조회
    *
    * 저장 및 갱신 시 : searchDate를 지정하여 해당 날짜의 데이터만 조회
    * 전체 목록 조회 시 : searchDate에 null을 전달하여 Redis의 모든 Plane 데이터를 조회
    * */
    public <T> List<T> scanRedis(String searchDate, Class<T> clazz) {

        // Redis plane-Hash 전체 조회
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(REDIS_PLANE_HASH_KEY);

        if (entries == null || entries.isEmpty()) {
            return List.of();
        }

        // redis 조회 시 반환할 리스트
        List<T> results = new ArrayList<>();

        for (Object value : entries.values()) {

            if (value == null) continue;

            T data;

            // 저장된 값이 객체인지, HashMap(Json)인지 체크
            if (clazz.isInstance(value)) {
                data = clazz.cast(value);
            } else if (value instanceof Map) {
                data = objectMapper.convertValue(value, clazz);
            } else continue;

            // 날짜 필터링, null이라면 전체 조회, 값이 있다면 특정 searchDate의 데이터만 조회
            if (searchDate == null) {
                results.add(data);

                continue;
            }

            String schedule = null;

            if (clazz == Plane.class) {
                schedule = ((Plane) data).getScheduleDateTime();
            } else if (clazz == PlaneResDto.class) {
                schedule = ((PlaneResDto) data).getScheduleDateTime();
            }

            if (schedule != null && schedule.startsWith(searchDate)) {
                results.add(data);
            }
        }

        return results;
    }

    /*
    * 모든 plane 데이터 조회
    * */
    private <T> List<T> getAllPlanes(Class<T> clazz) {

        List<Plane> planes = planeRepository.findAll();
        planes.forEach(this::saveToRedis);

        return planes.stream()
                .map(p -> convert(p, clazz))
                .toList();
    }

    /*
    * 특정 날짜의 plane 데이터 조회
    * */
    private <T> List<T> getPlanesBySearchDate(Class<T> clazz, String searchDate) {

        List<Plane> planes = planeRepository.findBySearchDate(searchDate);
        planes.forEach(this::saveToRedis);

        return planes.stream()
                .map(p -> convert(p, clazz))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private <T> T convert(Plane plane, Class<T> clazz) {

        if (clazz == Plane.class) {
            return (T) plane;
        }

        if (clazz == PlaneResDto.class) {
            return (T) convertToPlaneResDto(plane);
        }

        throw new IllegalArgumentException("지원하지 않는 타입: " + clazz);
    }

    private PlaneResDto convertToPlaneResDto(Plane plane) {
        return PlaneResDto.builder()
                .flightId(plane.getFlightId())
                .airLine(plane.getAirLine())
                .airport(plane.getAirport())
                .airportCode(plane.getAirportCode())
                .scheduleDateTime(plane.getScheduleDateTime())
                .estimatedDateTime(plane.getEstimatedDateTime())
                .gatenumber(plane.getGatenumber())
                .terminalid(plane.getTerminalid())
                .remark(plane.getRemark())
                .codeShare(plane.getCodeShare())
                .build();
    }

    private void saveToRedis(Plane plane) {
        String key = plane.getFlightId() + "_" + plane.getScheduleDateTime();

        redisTemplate.opsForHash().put(REDIS_PLANE_HASH_KEY, key, plane);
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
                .forEach(p -> {
                    String redisKey = "plane:" + p.getFlightId() + "_" + p.getScheduleDateTime();

                    redisTemplate.opsForHash().delete(REDIS_PLANE_HASH_KEY, redisKey);

                    planeRepository.delete(p);
                });
    }
}