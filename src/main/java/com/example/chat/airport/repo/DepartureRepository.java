package com.example.chat.airport.repo;

import com.example.chat.airport.entity.Departure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface DepartureRepository  extends JpaRepository<Departure, Long> {

}
