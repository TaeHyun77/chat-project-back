package com.example.chat.airport;

import com.example.chat.airport.Departure.Departure;
import com.example.chat.airport.plane.Plane;
import com.example.chat.airport.Departure.DepartureRepository;
import com.example.chat.airport.plane.PlaneRepository;
import com.example.chat.airport.Departure.DepartureDto;
import com.example.chat.airport.plane.PlaneDto;
import com.example.chat.airport.Departure.DepartureResDto;
import com.example.chat.airport.plane.PlaneResDto;
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
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AirportService {

    @Value("${data.api.key}") // 공공 데이터 API 키
    private String API_KEY;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final RedisTemplate<String, Object> redisTemplate;

    private final DepartureRepository departureRepository;
    private final PlaneRepository planeRepository;

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
                String departureDataEndPoint = "https://apis.data.go.kr/B551177/PassengerNoticeKR";

                URI uri = buildUri(departureDataEndPoint, searchDate);

                // JSON 형태로 받아옴
                String departureData = restTemplate.getForObject(uri, String.class);

                upsertDepartureData(departureData, searchDate);
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
    private void upsertDepartureData(String departureJsonData, String searchDate) throws JsonProcessingException {
        try {

            JsonNode items = parsePlaneJson(departureJsonData);

            for (JsonNode item : items) {

                String date = item.path("adate").asText();
                String timeZone = item.path("atime").asText();

                if (date.equals("합계")) {
                    date += "-" + searchDate;
                }

                DepartureDto dto = DepartureDto.builder()
                        .date(item.path("adate").asText())
                        .timeZone(item.path("atime").asText())
                        .t1Depart12(item.path("t1sum5").asLong())
                        .t1Depart3(item.path("t1sum6").asLong())
                        .t1Depart4(item.path("t1sum7").asLong())
                        .t1Depart56(item.path("t1sum8").asLong())
                        .t1DepartSum(item.path("t1sumset2").asLong())
                        .t2Depart1(item.path("t2sum3").asLong())
                        .t2Depart2(item.path("t2sum4").asLong())
                        .t2DepartSum(item.path("t2sumset2").asLong())
                        .build();

                Departure existsDepartureData = departureRepository.findByDateAndTimeZone(date, timeZone);
                existsDepartureData.updateDeparture(dto.toDepart());

                departureRepository.save(existsDepartureData);
            }
        } catch (Exception e) {
            log.error("출국장 데이터 저장 중 예외 발생: {}", e.getMessage());

            throw e;
        }
    }

    /*
    * 공항 항공편 현황 데이터를 갱신 및 저장
    *
    * 바뀔 수 있는 값 : estimatedDatetime, flightId, gateNumber, ** remark, terminalId
    * */
    @Transactional
    public void getPlane() {

        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        List<String> searchDates = List.of(
                today.format(formatter),          // 오늘 (D+0)
                today.plusDays(1).format(formatter),  // 내일 (D+1)
                today.plusDays(2).format(formatter)   // 모레 (D+2)
        );
        
        try {
            for (String searchDate : searchDates) {

                // 항공편 현황 데이터 조회 API end_point
                String planeDataEndPoint = "https://odp.airport.kr/openapi/Temp/StatusOfPassengerFlightsDeOdpTemp";
                URI uri = buildUri(planeDataEndPoint, searchDate);

                String jsonPlaneData = restTemplate.getForObject(uri, String.class);

                upsertPlaneData(jsonPlaneData, searchDate);
            }

        } catch (Exception e) {
            log.error("항공편 데이터 저장 중 예외 발생: {}", e.getMessage());

            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_PLANE_DATA);
        }
    }

    /*
    * 앞서 조회한 공항 항공편 현황 데이터를 DB에 저장 및 갱신, DB에는 없으면서 조회되지 않거나 출발 상태의 항공편은 삭제
    * */
    @Transactional
    private void upsertPlaneData(String jsonPlaneData, String searchDate) {
        try {
            log.info("json: "+ jsonPlaneData);
            JsonNode items = parsePlaneJson(jsonPlaneData);

            List<Plane> existPlaneData = planeRepository.findBySearchDate(searchDate);
            Map<String, Plane> existPlaneMap = existPlaneData.stream()
                    .collect(Collectors.toMap(p -> p.getFlightId() + "_" + p.getScheduleDatetime(), d -> d));

            Map<String, Plane> allPlanesCache = new HashMap<>();

            List<Plane> toSave = extractAndComparePlanes(items, existPlaneMap, allPlanesCache);

            // 삭제할 데이터 조회
            List<Plane> toDelete = existPlaneMap.values().stream()
                    .filter(p -> !allPlanesCache.containsKey(p.getFlightId() + "_" + p.getScheduleDatetime()))
                    .peek(p -> redisTemplate.delete("plane:" + p.getFlightId() + "_" + p.getScheduleDatetime()))
                    .collect(Collectors.toList());

            planeRepository.saveAll(toSave);
            planeRepository.deleteAll(toDelete);

        } catch (Exception e) {
            log.error("항공편 데이터 저장 예외 발생: {}", e.getMessage());
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_PLANE_DATA);
        }
    }

    private List<Plane> extractAndComparePlanes(JsonNode items, Map<String, Plane> existMap, Map<String, Plane> allPlanesCache) throws JsonProcessingException {
        List<Plane> toSave = new ArrayList<>();

        for (JsonNode item : items) {
            if (!"Master".equals(item.path("codeshare").asText())) continue;

            PlaneDto dto = PlaneDto.builder()
                    .flightId(item.path("flightId").asText())
                    .airLine(item.path("airline").asText())
                    .airport(item.path("airport").asText())
                    .airportCode(item.path("airportCode").asText())
                    .scheduleDatetime(item.path("scheduleDatetime").asText())
                    .estimatedDatetime(item.path("estimatedDatetime").asText())
                    .gateNumber(item.path("gateNumber").asText())
                    .terminalId(item.path("terminalId").asText())
                    .remark(item.path("remark").asText())
                    .aircraftRegNo(item.path("aircraftRegNo").asText())
                    .codeShare(item.path("codeshare").asText())
                    .build();

            String key = dto.getFlightId() + "_" + dto.getScheduleDatetime();
            Plane newPlane = dto.toPlane();
            allPlanesCache.put(key, newPlane);

            if (!existMap.containsKey(key)) {
                toSave.add(newPlane);
            } else if (planeDataChangeCheck(existMap.get(key),
                    dto.getRemark(), dto.getEstimatedDatetime(),
                    dto.getGateNumber(), dto.getTerminalId())) {

                existMap.get(key).updatePlane(dto.getRemark(), dto.getEstimatedDatetime(), dto.getGateNumber(), dto.getTerminalId());
                toSave.add(newPlane);
            }

            updateRedisCache(key, newPlane);
        }

        return toSave;
    }

    // 항공편 데이터 redis에 캐싱
    private void updateRedisCache(String key, Plane plane) {
        try {
            redisTemplate.opsForValue().set("plane:" + key, objectMapper.writeValueAsString(plane), 1, TimeUnit.HOURS);
        } catch (JsonProcessingException e) {
            log.warn("Redis 캐싱 실패: {}", key, e);
        }
    }

    // 항공편 데이터 변경 여부
    private boolean planeDataChangeCheck(Plane existingPlane, String remark , String estimatedDatetime, String gateNumber, String terminalId) {

        return !existingPlane.getRemark().equals(remark) || !existingPlane.getEstimatedDatetime().equals(estimatedDatetime) ||
                !existingPlane.getGateNumber().equals(gateNumber) || !existingPlane.getTerminalId().equals(terminalId);
    }

    // 모든 출국장 데이터 조회
    public List<DepartureResDto> getDepartures() {

        List<Departure> departures = departureRepository.findAll();

        return departures.stream()
                .map(d -> DepartureResDto.builder()
                        .date(d.getDate())
                        .timeZone(d.getTimeZone())
                        .t1Depart12(d.getT1Depart12())
                        .t1Depart3(d.getT1Depart3())
                        .t1Depart4(d.getT1Depart4())
                        .t1Depart56(d.getT1Depart56())
                        .t1DepartSum(d.getT1DepartSum())
                        .t2Depart1(d.getT2Depart1())
                        .t2Depart2(d.getT2Depart2())
                        .t2DepartSum(d.getT2DepartSum())
                        .build())
                .collect(Collectors.toList());
    }

    /*
    * 항공편 데이터 목록 조회
    *
    * 1. redis에 존재하면 redis에서 조회
    * 2. redis에서 조회할 수 없다면 DB에서 조회 후 redis에 캐싱
    * */
    public List<PlaneResDto> getAllPlanes(){
        List<PlaneResDto> redisPlanes = getPlanesFromRedis();

        if (!redisPlanes.isEmpty()) {
            log.info("REDIS에서 항공편 목록 조회 성공");

            return redisPlanes;
        }

        List<PlaneResDto> dbPlanes = getPlanesFromDb();
        log.info("DB에서 항공편 목록 조회 및 캐싱 완료");

        return dbPlanes;
    }

    // redis에서 항공편 목록 조회
    private List<PlaneResDto> getPlanesFromRedis() {
        List<PlaneResDto> planes = new ArrayList<>();

        ScanOptions options = ScanOptions.scanOptions()
                .match("plane:*")
                .count(1000) // 스캔 시 한 번에 1000개씩 가져와라
                .build();

        Cursor<byte[]> cursor = redisTemplate.execute((RedisConnection connection) ->
                connection.keyCommands().scan(options)
        );

        if (cursor != null) {
            try (cursor) {
                while (cursor.hasNext()) {
                    String key = new String(cursor.next(), StandardCharsets.UTF_8);
                    String json = (String) redisTemplate.opsForValue().get(key);

                    if (json != null) {
                        try {
                            planes.add(objectMapper.readValue(json, PlaneResDto.class));
                        } catch (JsonProcessingException e) {
                            log.warn("Redis PlaneResDto 역직렬화 실패: {}", key);
                        }
                    }
                }
            } catch (IOException e) {
                log.error("Redis Cursor 닫기 실패", e);
            }
        }

        return planes;
    }

    private List<PlaneResDto> getPlanesFromDb() {
        List<Plane> dbPlanes = planeRepository.findAll();

        ValueOperations<String, Object> redis = redisTemplate.opsForValue();

        dbPlanes.forEach(plane -> {
            try {
                String key = "plane:" + plane.getFlightId() + "_" + plane.getScheduleDatetime();

                redis.set(key, objectMapper.writeValueAsString(plane), 1, TimeUnit.HOURS);
            } catch (JsonProcessingException e) {
                log.warn("Plane 캐싱 실패: {}", plane.getFlightId(), e);
            }
        });

        return dbPlanes.stream()
                .map(this::convertToPlaneResDto)
                .collect(Collectors.toList());
    }

    private PlaneResDto convertToPlaneResDto(Plane plane) {
        return PlaneResDto.builder()
                .flightId(plane.getFlightId())
                .airLine(plane.getAirLine())
                .airport(plane.getAirport())
                .airportCode(plane.getAirportCode())
                .scheduleDatetime(plane.getScheduleDatetime())
                .estimatedDatetime(plane.getEstimatedDatetime())
                .gateNumber(plane.getGateNumber())
                .terminalId(plane.getTerminalId())
                .remark(plane.getRemark())
                .aircraftRegNo(plane.getAircraftRegNo())
                .codeShare(plane.getCodeShare())
                .build();
    }

    /*
    * OpenAPI 호출에 필요한 요청 URI를 반환
    * */
    private URI buildUri(String endPoint, String searchDate) throws URISyntaxException {

        // OpenAPI 요청 시 쿼리 파라미터를 URL 인코딩해야함
        String url = endPoint + "?"
                + "serviceKey=" + API_KEY
                + "&selectdate=" + URLEncoder.encode(searchDate, StandardCharsets.UTF_8)
                + "&numOfRows=" + URLEncoder.encode("9999", StandardCharsets.UTF_8)
                + "&type=" + URLEncoder.encode("json", StandardCharsets.UTF_8);

        return new URI(url);
    }

    private JsonNode parsePlaneJson(String jsonPlaneData) {
        if (jsonPlaneData == null || jsonPlaneData.isEmpty() || jsonPlaneData.startsWith("<")) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_CHANGE_JSON_DATE);
        }

        try {
            return objectMapper.readTree(jsonPlaneData)
                    .path("response").path("body").path("items");
        } catch (JsonProcessingException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_PARSE_JSON);
        }
    }
}
