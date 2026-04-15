package com.example.chat.airport.parking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingRepository extends JpaRepository<Parking, Long> {
    Optional<Parking> findByFloor(String floor);
}
