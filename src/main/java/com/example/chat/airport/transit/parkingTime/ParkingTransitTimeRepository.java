package com.example.chat.airport.transit.parkingTime;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingTransitTimeRepository extends JpaRepository<ParkingTransitTime, Long> {

    List<ParkingTransitTime> findByTerminalAndParkingNameAndZoneAndCheckInCounter(
            String terminal, String parkingName, String zone, String checkInCounter);

    List<ParkingTransitTime> findByTerminalAndCheckInCounter(String terminal, String checkInCounter);
}
