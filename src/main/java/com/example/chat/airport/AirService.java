package com.example.chat.airport;

import com.example.chat.airport.entity.Departure;
import com.example.chat.airport.entity.Plane;
import com.example.chat.airport.repository.DepartureRepository;
import com.example.chat.airport.repository.PlaneRepository;
import com.example.chat.airport.reqDto.DepartureDto;
import com.example.chat.airport.reqDto.PlaneDto;
import com.example.chat.airport.resDto.DepartureResDto;
import com.example.chat.airport.resDto.PlaneResDto;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
public class AirService {

    @Value("${data.api.key}") // 공공 데이터 API 키
    private String API_KEY;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final RedisTemplate<String, Object> redisTemplate;

    private final DepartureRepository departureRepository;
    private final PlaneRepository planeRepository;

    // 출국장 현황 데이터 조회 API end_point
    private final String departureDataEndPoint = "http://apis.data.go.kr/B551177/PassengerNoticeKR/getfPassengerNoticeIKR";

    // 항공편 현황 데이터 조회 API end_point
    private final String planeDataEndPoint = "http://apis.data.go.kr/B551177/statusOfAllFltDeOdp/getFltDeparturesDeOdp";

    /*
    * 공항 출국장 현황 데이터를 OpenAPI에서 조회
    *
    * Api 규칙 : searchDate = 0 (오늘), 1(내일), 2(모레) ...
    * */
    @Transactional
    public void getDepartureData() {

        List<String> searchDates = List.of("0", "1"); // 오늘, 내일

        try {
            for (String searchDate : searchDates) {

                URI uri = buildUri(departureDataEndPoint, searchDate);

                String departureData = restTemplate.getForObject(uri, String.class);

                upsertDepartureData(departureData, searchDate);
            }

        } catch (ChatException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_DEPARTURE_DATA);
        } catch (Exception e) {
            log.error("출국장 데이터 저장 예외 발생: {}", e.getMessage(), e);
        }
    }

    /*
    * 앞서 조회한 공항 출국장 현황 데이터를 DB에 저장 및 갱신, DB에는 존재하지만 조회되지 않는다면 삭제
    * */
    private void upsertDepartureData(String departureJsonData, String searchDate) {
        try {

            if (departureJsonData == null || departureJsonData.isEmpty() || departureJsonData.startsWith("<")) {
                throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_CHANGE_JSON_DATE);
            }

            JsonNode root = objectMapper.readTree(departureJsonData);
            // 오늘 또는 내일의 출국장 데이터
            JsonNode items = root.path("response").path("body").path("items");

            // DB에서 기존 데이터 조회
            List<Departure> existDeparture = departureRepository.findByDate(searchDate);
            Map<String, Departure> existDapartureMap = existDeparture.stream()
                    .collect(Collectors.toMap(d -> d.getDate() + "_" + d.getTimeZone(), d -> d));

            HashMap<String, Departure> departureData = new HashMap<>();
            List<Departure> toSave = new ArrayList<>();

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

                Departure apiDeparture = dto.toDepart();
                String key = date + "_" + timeZone;

                // 새 데이터
                if (!existDapartureMap.containsKey(key)) {
                    toSave.add(apiDeparture);
                }

                // 이미 존재하는 데이터 : Update
                else {
                    Departure existingEntity = existDapartureMap.get(key);

                    // 값이 다르면 Update (Entity에 update 메서드 필요)
                    if (!existingEntity.equals(apiDeparture)) {
                        existingEntity.updateDeparture(apiDeparture);
                        toSave.add(existingEntity);
                    }
                }

                departureData.put(key, apiDeparture);
            }

            // Delete 대상 ( DB에는 존재하지만, 새로 조회한 데이터에는 없는 데이터 )
            List<Departure> toDelete = existDeparture.stream()
                    .filter(d -> !departureData.containsKey(d.getDate() + "_" + d.getTimeZone()))
                    .toList();

            // 새로운 데이터 혹은 갱신되는 데이터 저장
            departureRepository.saveAll(toSave);
            departureRepository.deleteAll(toDelete);

        } catch (ChatException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_DEPARTURE_DATA);
        } catch (Exception e) {
            log.error("출국장 데이터 저장 예외 발생: {}", e.getMessage(), e);
        }
    }

    /*
    * 공항 항공편 현황 데이터를 OpenAPI에서 조회
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

                URI uri = buildUri(planeDataEndPoint, searchDate);

                String jsonPlaneData = restTemplate.getForObject(uri, String.class);

                upsertPlaneData(jsonPlaneData, searchDate);
            }

        } catch (ChatException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_PLANE_DATA);
        } catch (Exception e) {
            log.error("항공편 데이터 저장 예외 발생: {}", e.getMessage(), e);
        }
    }

    /*
    * 앞서 조회한 공항 항공편 현황 데이터를 DB에 저장 및 갱신, 조회되지 않거나 이미 출발한 항공편은 삭제
    * */
    @Transactional
    private void upsertPlaneData(String jsonPlaneData, String searchDate) {
        try {

            ValueOperations<String, Object> redis = redisTemplate.opsForValue();

            // jsonData가 "<"로 시작 한다는 에러가 발생하여 따로 처리
            if (jsonPlaneData == null || jsonPlaneData.isEmpty() || jsonPlaneData.startsWith("<")) {
                throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_CHANGE_JSON_DATE);
            }

            JsonNode root = objectMapper.readTree(jsonPlaneData);
            JsonNode items = root.path("response").path("body").path("items");

            // DB에서 기존 데이터 조회
            List<Plane> existPlaneData = planeRepository.findBySearchDate(searchDate);
            Map<String, Plane> existPlaneDataMap = existPlaneData.stream()
                    .collect(Collectors.toMap(p -> p.getFlightId() + "_" + p.getScheduleDatetime(), d -> d));

            // API에서 조회한 Plane 데이터
            Map<String, Plane> planeData = new HashMap<>();

            List<Plane> toSave = new ArrayList<>();
            for (JsonNode item : items) {

                // 본 항공편만 조회
                if (!"Master".equals(item.path("codeshare").asText())) continue;

                String remark = item.path("remark").asText();
                String estimatedDatetime = item.path("estimatedDatetime").asText();
                String gateNumber = item.path("gateNumber").asText();
                String terminalId = item.path("terminalId").asText();

                PlaneDto dto = PlaneDto.builder()
                        .flightId(item.path("flightId").asText())
                        .airLine(item.path("airline").asText())
                        .airport(item.path("airport").asText())
                        .airportCode(item.path("airportCode").asText())
                        .scheduleDatetime(item.path("scheduleDatetime").asText()) // 항공편 예정 시간
                        .estimatedDatetime(estimatedDatetime) // 항공편 출발 변경 시간
                        .gateNumber(gateNumber) // 항공편 탑승 gate 번호
                        .terminalId(terminalId)
                        .remark(remark) // 출발 여부
                        .aircraftRegNo(item.path("aircraftRegNo").asText())
                        .codeShare(item.path("codeshare").asText())
                        .build();

                String key = dto.getFlightId() + "_" + dto.getScheduleDatetime();
                Plane apiPlaneData = dto.toPlane();

                // 새로운 데이터
                if (!existPlaneDataMap.containsKey(key)) {
                    toSave.add(apiPlaneData);

                // 이미 존재하는 데이터 일 때, 바뀐 값이 있다면 갱신
                } else {
                    if (planeDataChangeCheck(existPlaneDataMap.get(key), remark, estimatedDatetime, gateNumber, terminalId)) {
                        existPlaneDataMap.get(key).updatePlane(remark, estimatedDatetime, gateNumber, terminalId);

                        toSave.add(apiPlaneData);
                    }
                }

                // redis 추가 or 갱신
                redis.set("plane:" + key, objectMapper.writeValueAsString(apiPlaneData), 1, TimeUnit.HOURS);

                planeData.put(key, apiPlaneData);
            }

            // 삭제할 데이터
            // DB에는 존재하지만 API 조회 데이터에는 존재하지 않을 때 삭제
            List<Plane> toDelete = existPlaneData.stream()
                    .filter(d -> !planeData.containsKey(d.getFlightId() + "_" + d.getScheduleDatetime()))
                    .toList();

            // Redis에서도 삭제
            toDelete.forEach(d -> {
                String redisKey = "plane:" + d.getFlightId() + "_" + d.getScheduleDatetime();

                redisTemplate.delete(redisKey);
            });

            planeRepository.saveAll(toSave);
            planeRepository.deleteAll(toDelete);

        } catch (ChatException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_PLANE_DATA);
        } catch (Exception e) {
            log.error("항공편 데이터 저장 예외 발생: {}", e.getMessage(), e);
        }
    }

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

    // 모든 항공편 데이터 조회 , 레디스 조회 후 없으면 DB에서 조회
    public List<PlaneResDto> getAllPlanes() {

        String cacheKey = "plane:*";

        // Redis에서 항공편 데이터 조회
        Set<String> keys = redisTemplate.keys(cacheKey);

        if (!keys.isEmpty()) {
            List<Object> redisPlanes = redisTemplate.opsForValue().multiGet(keys);

            List<PlaneResDto> planesFromRedis = Objects.requireNonNull(redisPlanes).stream()
                    .filter(Objects::nonNull)
                    .map(obj -> {
                        try {
                            return objectMapper.readValue((String) obj, PlaneResDto.class);
                        } catch (JsonProcessingException e) {
                            log.error("redis PlaneResDto 변환 중 오류 발생", e);
                            return null;
                        }
                    })
                    .collect(Collectors.toList());

            if (!planesFromRedis.isEmpty()) {
                log.info("redis Plane 데이터 조회");

                return planesFromRedis;
            }
        }

        // DB에서 조회 ( Redis에 데이터가 없으면 )
        ValueOperations<String, Object> redis = redisTemplate.opsForValue();

        List<Plane> planes = planeRepository.findAll();

        // redis 캐싱
        planes.forEach(plane ->
                {
                    try {
                        redis.set("plane:" + plane.getFlightId() + "_" + plane.getScheduleDatetime(), objectMapper.writeValueAsString(plane), 1, TimeUnit.HOURS);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        log.info("DB에서 Plane 데이터 조회");

        return planes.stream()
                .map(plane -> PlaneResDto.builder()
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
                        .build())
                .collect(Collectors.toList());
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

    /*@Transactional
    public void deleteAndInsertPlane() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String yesterday = LocalDateTime.now().minusDays(1).format(formatter);

        try {
            long deleteCnt = planeRepository.deleteByScheduleDateStartsWith(yesterday);
            log.info("삭제된 어제 항공편 데이터 개수 : {}", deleteCnt);
            log.info("어제 항공편 데이터 삭제 성공");
        } catch (ChatException e) {
            log.info("어제 항공편 데이터 삭제 실패");
        }
    }*/

    /*public Page<DepartureResDto> testPage(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return departureRepository.findAll(pageable).map(DepartureResDto::new);

    }*/
}
