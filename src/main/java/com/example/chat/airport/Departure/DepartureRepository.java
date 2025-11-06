package com.example.chat.airport.Departure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartureRepository extends JpaRepository<Departure, Long> {

    Departure findByDateAndTimeZone(String date, String timeZone);
}
