package com.example.chat.airport;

import com.example.chat.airport.dto.DepartureDto;
import com.example.chat.airport.dto.PlaneDto;
import com.example.chat.airport.entity.Departure;
import com.example.chat.airport.entity.Plane;
import com.example.chat.airport.repo.DepartureRepository;
import com.example.chat.airport.repo.PlaneRepository;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AirService {
    @Value("${data.api.key}") // 공공 데이터 API 키
    private String API_KEY;
    private final RestTemplate restTemplate = new RestTemplate();
    private final DepartureRepository departureRepository;
    private final PlaneRepository planeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisTemplate<String, Object> redisTemplate;


    @Transactional
    public void getArrivalsData() {

        departureRepository.deleteAll();

        List<String> selectdates = new ArrayList<>();
        selectdates.add("0"); // 오늘
        selectdates.add("1"); // 내일

        String endPoint = "http://apis.data.go.kr/B551177/PassengerNoticeKR/getfPassengerNoticeIKR";

        try {
            for (String selectdate : selectdates) {
                String url = endPoint + "?"
                        + "serviceKey=" + API_KEY
                        + "&selectdate=" + URLEncoder.encode(selectdate, StandardCharsets.UTF_8)
                        + "&numOfRows=" + URLEncoder.encode("9999", StandardCharsets.UTF_8)
                        + "&type=" + URLEncoder.encode("json", StandardCharsets.UTF_8);

                // restTemplate으로 보낼 때 인코딩 하고 보내야 된데
                URI uri = new URI(url);

                ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

                saveArrivalData(response.getBody(), selectdate);
            }

        } catch (ChatException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_ARRIVAL_DATA);
        } catch (Exception e) {
            log.error("출국장 데이터 저장 예외 발생: {}", e.getMessage(), e);
        }
    }

    /*
    바꿀 수 있는 값 : estimatedDatetime, flightId, gateNumber, ** remark, terminalId
    */
    @Transactional
    public void getPlane() {

        planeRepository.deleteAll();

        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        List<String> searchDates = new ArrayList<>();
        searchDates.add(today.format(formatter)); // 오늘 (D+0)
        searchDates.add(today.plusDays(1).format(formatter));  // 내일 (D+1)
        searchDates.add(today.plusDays(2).format(formatter));  // 내일모레 (D+2)

        String endPoint = "http://apis.data.go.kr/B551177/statusOfAllFltDeOdp/getFltDeparturesDeOdp";

        try {
            for (String searchDate : searchDates) {
                String url = endPoint + "?"
                        + "serviceKey=" + API_KEY
                        + "&numOfRows=" + URLEncoder.encode("9999", StandardCharsets.UTF_8)
                        + "&type=" + URLEncoder.encode("json", StandardCharsets.UTF_8)
                        + "&searchDate=" + URLEncoder.encode(searchDate, StandardCharsets.UTF_8);

                URI uri = new URI(url);

                ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
                savePlaneData(response.getBody(), today);
            }

        } catch (ChatException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_PLANE_DATA);
        } catch (Exception e) {
            log.error("항공편 데이터 저장 예외 발생: {}", e.getMessage(), e);
        }
    }

    private void saveArrivalData(String jsonData, String selectdate) {
        try {
            JsonNode root = objectMapper.readTree(jsonData);
            JsonNode items = root.path("response").path("body").path("items");

            List<Departure> departures = new ArrayList<>();

            for (JsonNode item : items) {

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

                if (item.path("adate").asText().equals("합계")) {
                    dto.setDate(item.path("adate").asText() + "-" + selectdate);
                }

                departures.add(dto.toDepart());
            }

            // 저장
            departureRepository.saveAll(departures);
        } catch (ChatException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_ARRIVAL_DATA);
        } catch (Exception e) {
            log.error("출국장 데이터 저장 예외 발생: {}", e.getMessage(), e);
        }
    }

    private void savePlaneData(String jsonData, LocalDateTime nowDateTime) {
        try {
            JsonNode root = objectMapper.readTree(jsonData);
            JsonNode items = root.path("response").path("body").path("items");

            List<Plane> planes = new ArrayList<>();

            ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();

            for (JsonNode item : items) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

                String codeshare = item.path("codeshare").asText(); // 본 항공편만 조회
                String scheduleDatetime = item.path("scheduleDatetime").asText(); // 항공편 예정 시간
                String remark = item.path("remark").asText(); // 출발 여부

                // 현재보다 출발 예정 시각이 이전이면서 "출발" 상태인 것은 x , Master가 아니면 x
                if ((scheduleDatetime.compareTo(nowDateTime.format(formatter)) < 0 && remark.equals("출발")) || (!codeshare.equals("Master"))) {
                    continue;
                }

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

                planes.add(dto.toPlane());

                String redisKey = "plane:" + dto.getFlightId() + ":" + dto.getScheduleDatetime();
                valueOps.set(redisKey, dto, 1, TimeUnit.HOURS); // TTL 1시간 설정
            }

            // 저장
            planeRepository.saveAll(planes);
        } catch (ChatException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_PLANE_DATA);
        } catch (Exception e) {
            log.error("항공편 데이터 저장 예외 발생: {}", e.getMessage(), e);
        }
    }

    public List<Departure> getDepartures() {
        return departureRepository.findAll();
    }

    public List<Plane> getPlanes() {
        return planeRepository.findAll();
    }

    public List<PlaneDto> getAllPlanesFromRedis() {
        Set<String> keys = redisTemplate.keys("plane:*"); // 모든 항공편 키 조회
        if (keys.isEmpty()) {
            return new ArrayList<>(); // 레디스에 데이터가 없으면 빈 리스트 반환
        }

        List<Object> planes = redisTemplate.opsForValue().multiGet(keys); // 여러 값 가져오기
        return planes.stream()
                .filter(Objects::nonNull)
                .map(obj -> (PlaneDto) obj) // 객체 변환
                .collect(Collectors.toList());
    }

    public List<PlaneDto> getAllPlanes() {
        List<PlaneDto> planes = getAllPlanesFromRedis(); // 먼저 Redis에서 조회

        if (planes.isEmpty()) { // 레디스에 데이터가 없으면 DB에서 조회
            planes = planeRepository.findAll().stream()
                    .map(PlaneDto::fromEntity) // Plane -> PlaneDto 변환
                    .collect(Collectors.toList());

            // 가져온 데이터를 Redis에 캐싱
            ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
            for (PlaneDto plane : planes) {
                String redisKey = "plane:" + plane.getFlightId();
                valueOps.set(redisKey, plane, 1, TimeUnit.HOURS);
            }
        }

        return planes;
    }
}
