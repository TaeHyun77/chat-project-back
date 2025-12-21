package com.example.chat.airport.Departure.repository;

import com.example.chat.airport.Departure.Departure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartureRepository extends JpaRepository<Departure, Long>, DepartureCustomRepository {

    Optional<Departure> findByDateAndTimeZone(String date, String timeZone);

    void deleteByDate(String date);
}
