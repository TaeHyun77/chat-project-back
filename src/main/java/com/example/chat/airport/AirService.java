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
import org.springframework.beans.factory.annotation.Value;
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

@RequiredArgsConstructor
@Service
public class AirService {
    @Value("${data.api.key}") // 공공 데이터 API 키
    private String API_KEY;
    private final RestTemplate restTemplate = new RestTemplate();
    private final DepartureRepository departureRepository;
    private final PlaneRepository planeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void getArrivalsData() {

        List<String> selectdates = new ArrayList<>();
        selectdates.add("0"); // 오늘
        selectdates.add("1"); // 내일

        departureRepository.deleteAll();

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
            e.printStackTrace();
        }
    }

    /*
    바꿀 수 있는 값 : estimatedDatetime, flightId, gateNumber, ** remark, terminalId
    */
    @Transactional
    public void getPlane() {

        List<String> searchDates = new ArrayList<>();

        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

        String yesterday = today.minusDays(1).format(formatter);  // 어제
        String now = today.format(formatter);  // 오늘

        // 어제 데이터 있으면 삭제
        if (planeRepository.existsByScheduleDatetime(yesterday)) {
            planeRepository.deleteByScheduleDatetime(yesterday);
        }

        // 내일(D+1)과 내일모레(D+2)는 항상 추가
        searchDates.add(today.plusDays(1).format(formatter));  // 내일 (D+1)
        searchDates.add(today.plusDays(2).format(formatter));  // 내일모레 (D+2)

        // 오늘(D+0) 데이터가 없으면 추가
        if (!planeRepository.existsByScheduleDatetime(now)) {
            searchDates.add(0, now);
        }

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
                savePlaneData(response.getBody());
            }

        } catch (ChatException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_PLANE_DATA);
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    private void savePlaneData(String jsonData) {
        try {
            JsonNode root = objectMapper.readTree(jsonData);
            JsonNode items = root.path("response").path("body").path("items");

            List<Plane> planes = new ArrayList<>();

            for (JsonNode item : items) {

                if (item.path("codeshare").asText().equals("Master")) {
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
                }
            }

            // 저장
            planeRepository.saveAll(planes);
        } catch (ChatException e) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_PLANE_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
