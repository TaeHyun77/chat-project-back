package com.example.chat.airport.repo;

import com.example.chat.airport.entity.Plane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PlaneRepository extends JpaRepository<Plane, Long> {

}
