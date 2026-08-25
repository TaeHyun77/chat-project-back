package com.example.chat.airport.search.application;
import com.example.chat.airport.search.domain.FlightSearchRepository;
import com.example.chat.airport.search.domain.FlightDocument;

import com.example.chat.airport.plane.domain.FlightIndexingEvent;
import com.example.chat.config.AsyncConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 항공편 인덱싱 이벤트를 받아 Elasticsearch에 문서를 반영하는 리스너.
 * 트랜잭션 커밋 이후 별도 스레드에서 실행된다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class FlightIndexingListener {
    private final FlightSearchRepository flightSearchRepository;

    @Async(AsyncConfig.EVENT_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFlightIndexing(FlightIndexingEvent event) {
        try {
            String docId = event.getFlightId() + "_" + event.getScheduleDateTime();
            String suggest = event.getFlightId() + " " + event.getAirLine()
                    + " " + event.getAirport() + " " + event.getAirportCode();

            FlightDocument document = FlightDocument.builder()
                    .id(docId)
                    .planeId(event.getPlaneId())
                    .flightId(event.getFlightId())
                    .airLine(event.getAirLine())
                    .airport(event.getAirport())
                    .airportCode(event.getAirportCode())
                    .scheduleDateTime(event.getScheduleDateTime())
                    .estimatedDateTime(event.getEstimatedDateTime())
                    .gatenumber(event.getGatenumber())
                    .terminalid(event.getTerminalid())
                    .remark(event.getRemark())
                    .searchDate(event.getSearchDate())
                    .suggest(suggest)
                    .build();

            flightSearchRepository.save(document);
            log.info("항공편 ES 인덱싱 완료: {}", docId);
        } catch (Exception e) {
            log.error("항공편 ES 인덱싱 실패: flightId={}", event.getFlightId(), e);
        }
    }
}
