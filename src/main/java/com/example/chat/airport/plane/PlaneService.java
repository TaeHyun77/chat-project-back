package com.example.chat.airport.plane;

import com.example.chat.airport.plane.dto.PlaneReqDto;
import com.example.chat.airport.plane.dto.PlaneResDto;
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

    private final PlaneRepository planeRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /*
    * API를 통해 조회한 공항 항공편 현황 데이터를 DB에 저장 및 갱신
    * */
    @Transactional
    public void upsertPlaneData(JsonNode jsonPlaneData, String searchDate) {
        try {
            List<Plane> existPlaneDb = getPlanesBySearchDate(searchDate);

            // 기존에 존재하는 항공편 데이터를 Map에 key-value 형태로 저장
            // key : FlightId + scheduleDateTime
            Map<String, Plane> existPlaneDbMap = existPlaneDb.stream()
                    .collect(Collectors.toMap(p -> p.getFlightId() + "_" + p.getScheduleDateTime(), p -> p));

            // DB에 저장할 항공편 데이터를 담는 리스트
            List<Plane> toSave = savePlane(jsonPlaneData, existPlaneDbMap, searchDate);

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
    private List<Plane> savePlane(JsonNode items, Map<String, Plane> existPlaneDbMap, String searchDate) {

        String yesterday = LocalDate.now().minusDays(1).format(formatter);

        // DB에 저장할 항공편 데이터를 담는 리스트
        List<Plane> planesToSave = new ArrayList<>();

        // JSON 데이터에서 같은 데이터가 여러 개 들어오는 경우가 있기에 중복을 제거하기 위함
        Set<String> checkDuplication = new HashSet<>();

        // API에서 가져온 데이터를 순회하여 처리
        for (JsonNode item : items) {
            String codeshare = item.path("codeshare").asText();

            if (!codeshare.equals("Master")) continue;

            Plane plane = Plane.builder()
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

            String key = plane.getFlightId() + "_" + plane.getScheduleDateTime();

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
                // JPA 더티 체킹
                existingPlane.updatePlane(plane.getRemark(), plane.getEstimatedDateTime(), plane.getGatenumber(), plane.getTerminalid(), plane.getChkinrange());

            } else { // DB에 존재하지 않는 경우
                if (searchDate.equals(yesterday)) continue;

                planesToSave.add(plane);
            }
        }

        return planesToSave;
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