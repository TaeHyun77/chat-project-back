package com.example.chat.airport.plane;

import com.example.chat.airport.plane.dto.PlaneResDto;
import com.example.chat.airport.kafka.message.PlaneChangedMessage;
import com.example.chat.airport.kafka.message.PlaneIndexingMessage;
import com.example.chat.airport.plane.repository.PlaneRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlaneService {

    private static final String TOPIC_INDEXING = "airport.plane.indexing";
    private static final String TOPIC_CHANGED = "airport.plane.changed";

    private final PlaneRepository planeRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    // API를 통해 조회한 공항 항공편 현황 데이터를 DB에 저장 및 갱신
    @Transactional
    public void upsertPlaneData(JsonNode jsonPlaneData, String searchDate) {
        // DB에 존재하는 특정 searchDate 항공편 데이터 전부 가져오기
        List<Plane> existPlaneDb = getPlanesBySearchDate(searchDate);

        // 기존에 존재하는 항공편 데이터를 Map에 key-value 형태로 저장
        // key : "FlightId"_"scheduleDateTime" 형태
        Map<String, Plane> existPlaneDbMap = existPlaneDb.stream()
                .collect(Collectors.toMap(
                        p -> p.getFlightId() + "_" + p.getScheduleDateTime(),
                        p -> p,
                        (a, b) -> a
                ));

        // DB에 저장할 신규 항공편 데이터
        List<Plane> toSave = new ArrayList<>();
        processPlaneItems(jsonPlaneData, existPlaneDbMap, searchDate, toSave);

        planeRepository.saveAll(toSave);
    }

    /*
     * API를 통해 조회한 공항 항공편 현황 데이터를 실제로 처리하는 로직
     *
     * 어제 항공편 데이터를 포함하는 이유는 지연이나 결항 등의 사유로 일정이 다음 날로 변경될 수 있기 때문이며,
     * 이 경우 새로운 데이터로 저장하지 않고 기존 정보의 갱신 여부만 확인
     */
    private void processPlaneItems(JsonNode items, Map<String, Plane> existPlaneDbMap, String searchDate, List<Plane> planesToSave) {

        // API 응답 내 동일 항공편 중복 제거용 ( JSON 데이터 중 중복되는 정보가 있는 경우가 있기에 )
        Set<String> checkDuplication = new HashSet<>();

        for (JsonNode item : items) {
            // key 값
            String key = item.path("flightId").asText() + "_" + item.path("scheduleDateTime").asText();

            // 항공편 중 codeshare 값이 "Master"가 아니라면 유효하지 않은 것이므로 제외
            if (!item.path("codeshare").asText().equals("Master")) continue;

            // 중복 데이터 제외
            if (!checkDuplication.add(key)) continue;

            // 이미 DB에 존재하는 항공편인지 확인
            Plane existingPlane = existPlaneDbMap.get(key);

            // 이미 DB에 존재하는 항공편이라면 수정 여부 파악
            if (existingPlane != null) {
                String newRemark = item.path("remark").asText();
                String newEstimatedDateTime = item.path("estimatedDateTime").asText();
                String newGatenumber = item.path("gatenumber").asText();
                String newTerminalId = item.path("terminalid").asText();
                String newChkinrange = item.path("chkinrange").asText();

                boolean changed = hasPlaneChanged(
                        existingPlane,
                        newRemark,
                        newEstimatedDateTime,
                        newGatenumber,
                        newTerminalId,
                        newChkinrange
                );

                // 데이터가 변경되었다면
                if (changed) {
                    // 알림용 Kafka 메시지 발행 (existingPlane은 아직 변경 전 상태 → prev 값으로 사용)
                    kafkaTemplate.send(TOPIC_CHANGED, existingPlane.getFlightId(),
                            PlaneChangedMessage.from(existingPlane, newRemark, newEstimatedDateTime, newGatenumber, newTerminalId, newChkinrange));

                    // Dirty Checking으로 수정
                    existingPlane.updatePlane(
                            newRemark,
                            newEstimatedDateTime,
                            newGatenumber,
                            newTerminalId,
                            newChkinrange
                    );

                    // ES 인덱싱용 Kafka 메시지 즉시 발행
                    kafkaTemplate.send(TOPIC_INDEXING, existingPlane.getFlightId(),
                            PlaneIndexingMessage.from(existingPlane));
                }

            // 신규 데이터
            } else {
                // 어제 날짜의 신규 데이터는 제외
                LocalDate targetDate = LocalDate.parse(searchDate, formatter);
                if (targetDate.equals(LocalDate.now().minusDays(1))) continue;

                Plane newPlane = Plane.builder()
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

                planesToSave.add(newPlane);

                // ES 인덱싱용 Kafka 메시지 즉시 발행
                kafkaTemplate.send(TOPIC_INDEXING, newPlane.getFlightId(),
                        PlaneIndexingMessage.from(newPlane));
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

    // 스케줄러를 통해 매 자정에 어제 항공편 삭제 ( 어제 날짜이면서 출발 완료 상태의 항공편만 삭제 )
    @Transactional
    public void cleanUpPlaneDataOlderThanTwoDays() {
        String twoDaysAgoData = LocalDate.now().minusDays(2).format(formatter);

        planeRepository.deleteBySearchDateAndRemark(twoDaysAgoData, "출발");
    }
}
