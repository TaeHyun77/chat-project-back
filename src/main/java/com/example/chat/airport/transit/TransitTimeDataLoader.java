package com.example.chat.airport.transit;

import com.example.chat.airport.transit.arexTime.ArexTransitTime;
import com.example.chat.airport.transit.arexTime.ArexTransitTimeRepository;
import com.example.chat.airport.transit.parkingTime.ParkingTransitTime;
import com.example.chat.airport.transit.parkingTime.ParkingTransitTimeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransitTimeDataLoader implements ApplicationRunner {

    private final ArexTransitTimeRepository arexRepository;
    private final ParkingTransitTimeRepository parkingRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        loadArexTransitTime();
        loadParkingTransitTime();
    }

    private void loadArexTransitTime() {
        if (arexRepository.count() > 0) {
            log.info("공항철도 소요시간 데이터 이미 존재, 로딩 건너뜀");
            return;
        }

        try (InputStream is = new ClassPathResource("data/arex-transit-time.json").getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            List<ArexTransitTime> list = new ArrayList<>();

            for (JsonNode node : root) {
                String terminal = node.path("terminal").asText();
                String stationName = node.path("stationName").asText();
                String checkInCounter = node.path("checkInCounter").asText();
                int travelSeconds = node.path("travelSeconds").asInt();

                list.add(ArexTransitTime.builder()
                        .terminal(terminal)
                        .stationName(stationName)
                        .checkInCounter(checkInCounter)
                        .travelSeconds(travelSeconds)
                        .build());
            }

            arexRepository.saveAll(list);
            log.info("공항철도 소요시간 데이터 {}건 로딩 완료", list.size());
        } catch (Exception e) {
            log.error("공항철도 소요시간 데이터 로딩 실패", e);
        }
    }

    private void loadParkingTransitTime() {
        if (parkingRepository.count() > 0) {
            log.info("주차장 소요시간 데이터 이미 존재, 로딩 건너뜀");
            return;
        }

        try (InputStream is = new ClassPathResource("data/parking-transit-time.json").getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            List<ParkingTransitTime> list = new ArrayList<>();

            for (JsonNode node : root) {
                String terminal = node.path("terminal").asText();
                String parkingName = node.path("parkingName").asText();
                String zone = node.path("zone").asText();
                String checkInCounter = node.path("checkInCounter").asText();
                int travelSeconds = node.path("travelSeconds").asInt();

                list.add(ParkingTransitTime.builder()
                        .terminal(terminal)
                        .parkingName(parkingName)
                        .zone(zone)
                        .checkInCounter(checkInCounter)
                        .travelSeconds(travelSeconds)
                        .build());
            }

            parkingRepository.saveAll(list);
            log.info("주차장 소요시간 데이터 {}건 로딩 완료", list.size());
        } catch (Exception e) {
            log.error("주차장 소요시간 데이터 로딩 실패", e);
        }
    }
}
