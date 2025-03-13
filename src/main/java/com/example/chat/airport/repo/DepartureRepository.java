package com.example.chat.airport.repo;

import com.example.chat.airport.entity.Departure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartureRepository  extends JpaRepository<Departure, Long> {

}
