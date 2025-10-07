package com.example.chat.airport.repository;

import com.example.chat.airport.entity.Departure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartureRepository extends JpaRepository<Departure, Long> {

    // Page<Departure> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Departure> findByDate(String date);

}
