package com.example.chat.airport.plane;

import com.example.chat.airport.plane.dto.PlaneResDto;
import com.example.chat.airport.plane.event.FlightIndexingEvent;
import com.example.chat.airport.plane.event.PlaneChangedEvent;
import com.example.chat.common.DateUtils;
import com.example.chat.airport.plane.repository.PlaneRepository;
import com.example.chat.airport.search.FlightSearchService;
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

import java.time.LocalDate;
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

    /*
     * API를 통해 조회한 공항 항공편 현황 데이터를 실제로 처리하는 로직
     *
     * 어제 항공편 데이터를 포함하는 이유는 지연이나 결항 등의 사유로 일정이 다음 날로 변경될 수 있기 때문이며,
     * 이 경우 새로운 데이터는 저장하지 않고 갱신 여부만 확인
     */
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
                buildNewPlaneIfApplicable(item, searchDate).ifPresent(planesToSave::add);
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

    // 신규 항공편 생성 — 어제 날짜의 신규 데이터는 제외
    private Optional<Plane> buildNewPlaneIfApplicable(JsonNode item, String searchDate) {
        LocalDate targetDate = LocalDate.parse(searchDate, DateUtils.BASIC_DATE);
        if (targetDate.equals(LocalDate.now().minusDays(1))) return Optional.empty();

        return Optional.of(Plane.builder()
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
