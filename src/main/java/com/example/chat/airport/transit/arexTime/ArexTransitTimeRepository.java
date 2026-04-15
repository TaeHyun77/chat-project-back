package com.example.chat.airport.transit.arexTime;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArexTransitTimeRepository extends JpaRepository<ArexTransitTime, Long> {

    List<ArexTransitTime> findByTerminalAndCheckInCounter(String terminal, String checkInCounter);

    List<ArexTransitTime> findByTerminalAndStationNameAndCheckInCounter(
            String terminal, String stationName, String checkInCounter);
}
