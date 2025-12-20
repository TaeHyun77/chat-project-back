package com.example.chat.airport.plane;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaneRepository extends JpaRepository<Plane, Long>, PlaneRepositoryCustom{

    List<Plane> findBySearchDate(String searchDate);

    Slice<Plane> findBySearchDate(String searchDate, Pageable pageable);
}

