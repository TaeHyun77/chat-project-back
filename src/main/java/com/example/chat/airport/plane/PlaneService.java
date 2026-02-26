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
            // key : FlightId + scheduleDateTime / DB 중복 데이터 존재 시 첫 번째 항목 유지
            Map<String, Plane> existPlaneDbMap = existPlaneDb.stream()
                    .collect(Collectors.toMap(
                            p -> p.getFlightId() + "_" + p.getScheduleDateTime(),
                            p -> p,
                            (a, b) -> a
                    ));

            // DB에 저장할 항공편 데이터를 담는 리스트
            List<Plane> toSave = savePlane(jsonPlaneData, existPlaneDbMap, searchDate);

            planeRepository.saveAll(toSave);

        } catch (Exception e) {
            log.error("항공편 데이터 저장 예외 발생", e);
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.ERROR_TO_SAVE_PLANE_DATA);
        }
    }

    /*
     * API를 통해 조회한 공항 항공편 현황 데이터를 실제로 처리하는 로직
     *
     * 어제 항공편 데이터를 포함하는 이유는 지연이나 결항 등의 사유로 일정이 다음 날로 변경될 수 있기 때문이며,
     * 이 경우 새로운 데이터로 저장하지 않고 기존 정보의 갱신 여부만 확인
     */
    private List<Plane> savePlane(JsonNode items, Map<String, Plane> existPlaneDbMap, String searchDate) {

        String yesterday = LocalDate.now().minusDays(1).format(formatter);

        List<Plane> planesToSave = new ArrayList<>();

        // API 응답 내 동일 항공편 중복 제거용 (Set.add() 반환값으로 이미 처리된 key 판별)
        Set<String> checkDuplication = new HashSet<>();

        for (JsonNode item : items) {
            if (!item.path("codeshare").asText().equals("Master")) continue;

            // Plane 객체 생성 전에 key를 먼저 추출하여 불필요한 객체 생성 방지
            String key = item.path("flightId").asText() + "_" + item.path("scheduleDateTime").asText();

            // 중복 데이터는 건너뜀
            if (!checkDuplication.add(key)) continue;

            Plane existingPlane = existPlaneDbMap.get(key);

            if (existingPlane != null) {
                // 업데이트에 필요한 5개 필드만 item에서 직접 읽어 처리
                existingPlane.updatePlane(
                        item.path("remark").asText(),
                        item.path("estimatedDateTime").asText(),
                        item.path("gatenumber").asText(),
                        item.path("terminalid").asText(),
                        item.path("chkinrange").asText()
                );
            } else {
                // 어제 날짜의 신규 데이터는 저장하지 않음
                if (searchDate.equals(yesterday)) continue;

                // 진짜 신규 데이터만 Plane 객체 생성
                planesToSave.add(Plane.builder()
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
                        .build());
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
     * 오늘/내일/모레 데이터가 아니고 출발 상태인 데이터만 일괄 삭제
     */
    @Transactional
    public void cleanUpPlaneData() {

        LocalDate today = LocalDate.now();

        // 유지할 날짜 목록 (오늘, 내일, 모레)
        List<String> retainDates = List.of(
                today.format(formatter),
                today.plusDays(1).format(formatter),
                today.plusDays(2).format(formatter)
        );

        planeRepository.deleteOldDepartedPlanes(retainDates);
    }
}