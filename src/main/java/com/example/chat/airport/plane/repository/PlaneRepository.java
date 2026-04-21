package com.example.chat.airport.plane.repository;

import com.example.chat.airport.plane.Plane;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaneRepository extends JpaRepository<Plane, Long> {

    List<Plane> findBySearchDate(String searchDate);

    // searchDate, remark를 지정하여 항공편 삭제
    void deleteBySearchDateAndRemark(String searchDate, String remark);

    Slice<Plane> findBySearchDate(String searchDate, Pageable pageable);
}

