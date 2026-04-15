package com.example.chat.airport.kafka;

import com.example.chat.airport.kafka.message.PlaneIndexingMessage;
import com.example.chat.airport.search.FlightDocument;
import com.example.chat.airport.search.FlightSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class FlightIndexingConsumer {

    private final FlightSearchRepository flightSearchRepository;

    @KafkaListener(topics = "airport.plane.indexing", groupId = "flight-es-indexer")
    public void onPlaneIndexing(PlaneIndexingMessage message) {
        try {
            String docId = message.getFlightId() + "_" + message.getScheduleDateTime();
            String suggest = message.getFlightId() + " " + message.getAirLine()
                    + " " + message.getAirport() + " " + message.getAirportCode();

            FlightDocument document = FlightDocument.builder()
                    .id(docId)
                    .flightId(message.getFlightId())
                    .airLine(message.getAirLine())
                    .airport(message.getAirport())
                    .airportCode(message.getAirportCode())
                    .scheduleDateTime(message.getScheduleDateTime())
                    .estimatedDateTime(message.getEstimatedDateTime())
                    .gatenumber(message.getGatenumber())
                    .terminalid(message.getTerminalid())
                    .remark(message.getRemark())
                    .searchDate(message.getSearchDate())
                    .suggest(suggest)
                    .build();

            flightSearchRepository.save(document);
            log.debug("항공편 ES 인덱싱 완료: {}", docId);
        } catch (Exception e) {
            log.error("항공편 ES 인덱싱 실패: flightId={}", message.getFlightId(), e);
        }
    }
}
