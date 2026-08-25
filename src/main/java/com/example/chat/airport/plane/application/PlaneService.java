package com.example.chat.airport.plane.application;
import com.example.chat.airport.plane.domain.Plane;

import com.example.chat.airport.plane.ui.PlaneResDto;
import com.example.chat.airport.plane.domain.FlightIndexingEvent;
import com.example.chat.airport.plane.domain.PlaneChangedEvent;
import com.example.chat.common.DateUtils;
import com.example.chat.airport.plane.domain.PlaneRepository;
import com.example.chat.airport.search.application.FlightSearchService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class PlaneService {
    private final PlaneRepository planeRepository;
    private final FlightSearchService flightSearchService;
    private final ApplicationEventPublisher eventPublisher;

    // API를 통해 조회한 공항 항공편 현황 데이터를 DB에 저장 및 갱신
    @Transactional
    public void upsertPlaneData(JsonNode jsonPlaneData, String searchDate) {
        List<Plane> existPlaneDb = getPlanesBySearchDate(searchDate);

        Map<String, Plane> existPlaneDbMap = existPlaneDb.stream()
                .collect(Collectors.toMap(
                        p -> p.getFlightId() + "_" + p.getScheduleDateTime(),
                        p -> p,
                        (a, b) -> a
                ));

        List<Plane> toSave = new ArrayList<>();
        processPlaneItems(jsonPlaneData, existPlaneDbMap, searchDate, toSave);

        planeRepository.saveAll(toSave);

        // 신규 항공편 ES 인덱싱 이벤트 발행 (saveAll 이후 ID가 생성되므로 이 시점에 생성)
        for (Plane plane : toSave) {
            eventPublisher.publishEvent(FlightIndexingEvent.from(plane));
        }
    }

    private void processPlaneItems(JsonNode items, Map<String, Plane> existPlaneDbMap, String searchDate, List<Plane> planesToSave) {
        // API 응답 내 동일 항공편 중복 제거용 (JSON 데이터 중 중복되는 정보가 있는 경우가 있기 때문)
        Set<String> checkDuplication = new HashSet<>();

        for (JsonNode item : items) {
            // codeshare 값이 "Master"가 아니라면 유효하지 않은 항공편이므로 제외
            if (!item.path("codeshare").asText().equals("Master")) continue;

            String key = item.path("flightId").asText() + "_" + item.path("scheduleDateTime").asText();

            if (!checkDuplication.add(key)) continue;

            Plane existingPlane = existPlaneDbMap.get(key);

            if (existingPlane != null) {
                updateExistingIfChanged(existingPlane, item);
            } else {
                planesToSave.add(buildNewPlane(item, searchDate));
            }
        }
    }

    // 기존 항공편 변경 여부를 확인하고, 변경된 경우에만 업데이트 및 이벤트 발행
    private void updateExistingIfChanged(Plane existingPlane, JsonNode item) {
        String newRemark = item.path("remark").asText();
        String newEstimatedDateTime = item.path("estimatedDateTime").asText();
        String newGatenumber = item.path("gatenumber").asText();
        String newTerminalId = item.path("terminalid").asText();
        String newChkinrange = item.path("chkinrange").asText();

        if (!hasPlaneChanged(existingPlane, newRemark, newEstimatedDateTime, newGatenumber, newTerminalId, newChkinrange)) {
            return;
        }

        existingPlane.updatePlane(newRemark, newEstimatedDateTime, newGatenumber, newTerminalId, newChkinrange);
        eventPublisher.publishEvent(PlaneChangedEvent.from(existingPlane, newRemark, newEstimatedDateTime, newGatenumber));
        eventPublisher.publishEvent(FlightIndexingEvent.from(existingPlane));
    }

    private Plane buildNewPlane(JsonNode item, String searchDate) {
        return Plane.builder()
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
    }

    // 어제 날짜 항공편 상태만 갱신 (지연·결항 추적용, 신규 insert 없음)
    @Transactional
    public void updatePlaneStatus(JsonNode jsonPlaneData, String searchDate) {
        List<Plane> existPlanes = getPlanesBySearchDate(searchDate);
        Map<String, Plane> existPlaneMap = existPlanes.stream()
                .collect(Collectors.toMap(
                        p -> p.getFlightId() + "_" + p.getScheduleDateTime(),
                        p -> p,
                        (a, b) -> a
                ));

        Set<String> checked = new HashSet<>();
        for (JsonNode item : jsonPlaneData) {
            if (!item.path("codeshare").asText().equals("Master")) continue;
            String key = item.path("flightId").asText() + "_" + item.path("scheduleDateTime").asText();
            if (!checked.add(key)) continue;

            Plane existing = existPlaneMap.get(key);
            if (existing != null) {
                updateExistingIfChanged(existing, item);
            }
        }
    }

    // Plane 엔티티 변경 여부
    private boolean hasPlaneChanged(
            Plane existingPlane,
            String newRemark,
            String newEstimatedDateTime,
            String newGatenumber,
            String newTerminalId,
            String newChkinrange
    ) {
        return !Objects.equals(existingPlane.getRemark(), newRemark)
                || !Objects.equals(existingPlane.getEstimatedDateTime(), newEstimatedDateTime)
                || !Objects.equals(existingPlane.getGatenumber(), newGatenumber)
                || !Objects.equals(existingPlane.getTerminalid(), newTerminalId)
                || !Objects.equals(existingPlane.getChkinrange(), newChkinrange);
    }

    public List<Plane> getPlanesBySearchDate(String searchDate) {
        return planeRepository.findBySearchDate(searchDate);
    }

    public Slice<PlaneResDto> getSlicePlanesBySearchDate(String date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("scheduleDateTime").ascending());
        Slice<Plane> slicePlane = planeRepository.findBySearchDate(date, pageable);
        return slicePlane.map(PlaneResDto::from);
    }

    @Transactional
    public void deleteAll() {
        planeRepository.deleteAll();
    }

    // DB의 모든 항공편 데이터를 이벤트로 재발행하여 ES 재인덱싱
    @Transactional
    public int reindexAll() {
        List<Plane> allPlanes = planeRepository.findAll();

        for (Plane plane : allPlanes) {
            eventPublisher.publishEvent(FlightIndexingEvent.from(plane));
        }

        log.info("ES 재인덱싱 이벤트 발행 완료: {}건", allPlanes.size());
        return allPlanes.size();
    }
}
