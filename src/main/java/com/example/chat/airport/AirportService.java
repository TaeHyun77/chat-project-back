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
import java.time.LocalDate;
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
    private void upsertDepartureData(String departureJsonData, String searchDate) {
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
                        .t1Depart1(item.path("t1dg1").asLong())
                        .t1Depart2(item.path("t1dg2").asLong())
                        .t1Depart3(item.path("t1dg3").asLong())
                        .t1Depart4(item.path("t1dg4").asLong())
                        .t1Depart5(item.path("t1dg5").asLong())
                        .t1Depart6(item.path("t1dg6").asLong())
                        .t1DepartSum(item.path("t1dgsum1").asLong())
                        .t2Depart1(item.path("t2dg1").asLong())
                        .t2Depart2(item.path("t2dg2").asLong())
                        .t2DepartSum(item.path("t2dgsum2").asLong())
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
    * 앞서 조회한 공항 항공편 현황 데이터를 DB에 저장 및 갱신
    * */
    @Transactional
    private void upsertPlaneData(String jsonPlaneData, String searchDate) {
        try {
            JsonNode items = parsePlaneJson(jsonPlaneData);

            // 기존에 존재하는 항공편 데이터
            List<Plane> existPlaneDb = planeRepository.findBySearchDate(searchDate);
            Map<String, Plane> existPlaneDbMap = existPlaneDb.stream()
                    .collect(Collectors.toMap(p -> p.getFlightId() + "_" + p.getScheduleDateTime(), d -> d));

            // 저장할 항공편 데이터를 담는 리스트
            List<Plane> toSave = toSavePlane(items, existPlaneDbMap, searchDate);

            planeRepository.saveAll(toSave);

        } catch (Exception e) {
            log.error("항공편 데이터 저장 예외 발생: {}", e.getMessage());
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_PLANE_DATA);
        }
    }

    private List<Plane> toSavePlane(JsonNode items, Map<String, Plane> existPlaneDbMap, String searchDate) {
        List<Plane> toSave = new ArrayList<>();

        // JSON 데이터에서 같은 데이터가 여러 개 들어오는 경우가 있기에 이를 체크
        Set<String> checkDuplication = new HashSet<>();

        for (JsonNode item : items) {
            if (!"Master".equals(item.path("codeshare").asText())) continue;

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

            if (checkDuplication.contains(key)) continue;

            Plane apiPlane = dto.toPlane();
            checkDuplication.add(key);

            // DB에 존재하지 않은 새로운 데이터라면 저장
            if (!existPlaneDbMap.containsKey(key)) {
                toSave.add(apiPlane);

                redisTemplate.opsForValue().set("plane:" + key, apiPlane, 1, TimeUnit.HOURS);

            // 이미 DB에 존재하는 데이터라면 변경 사항이 있을 때만 갱신하여 저장
            } else if (planeDataChangeCheck(existPlaneDbMap.get(key), dto.getRemark(), dto.getEstimatedDateTime(), dto.getGatenumber(), dto.getTerminalid())) {

                Plane existPlane = existPlaneDbMap.get(key);
                existPlane.updatePlane(dto.getRemark(), dto.getEstimatedDateTime(), dto.getGatenumber(), dto.getTerminalid());
                toSave.add(existPlane);

                redisTemplate.opsForValue().set("plane:" + key, apiPlane, 1, TimeUnit.HOURS);
            }
        }

        return toSave;
    }

    // 항공편 데이터 변경 여부
    private boolean planeDataChangeCheck(Plane existingPlane, String remark , String estimatedDatetime, String gateNumber, String terminalId) {

        return !existingPlane.getRemark().equals(remark) || !existingPlane.getEstimatedDateTime().equals(estimatedDatetime) ||
                !existingPlane.getGatenumber().equals(gateNumber) || !existingPlane.getTerminalid().equals(terminalId);
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
            String key = "plane:" + plane.getFlightId() + "_" + plane.getScheduleDateTime();

            redis.set(key, plane, 1, TimeUnit.HOURS);
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
                .scheduleDateTime(plane.getScheduleDateTime())
                .estimatedDateTime(plane.getEstimatedDateTime())
                .gatenumber(plane.getGatenumber())
                .terminalid(plane.getTerminalid())
                .remark(plane.getRemark())
                .codeShare(plane.getCodeShare())
                .build();
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

    @Transactional
    public void cleanUpPlaneData() {

        String today = LocalDate.now().format(formatter);
        String tomorrow = LocalDate.now().plusDays(1).format(formatter);
        String dayAfterTomorrow = LocalDate.now().plusDays(2).format(formatter);

        List<Plane> planes = planeRepository.findAll();

        planes.stream()
                .filter(p -> "출발".equals(p.getRemark()))
                .filter(p -> {
                    String searchDate = p.getSearchDate();

                    return !searchDate.equals(today) && !searchDate.equals(tomorrow) && !searchDate.equals(dayAfterTomorrow);
                })
                .forEach(p -> {
                    String redisKey = "plane:" + p.getFlightId() + "_" + p.getScheduleDateTime();
                    redisTemplate.delete(redisKey);

                    planeRepository.delete(p);
                });
    }
}
