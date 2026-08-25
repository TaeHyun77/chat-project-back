package com.example.chat.airport.departure.domain;

import com.example.chat.airport.departure.domain.Departure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartureRepository extends JpaRepository<Departure, Long> {
    Optional<Departure> findByDateAndTimeZone(String date, String timeZone);
    void deleteByDateBefore(String date);
}
