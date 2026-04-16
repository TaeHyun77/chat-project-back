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

    @Modifying
    @Query("DELETE FROM Plane p WHERE p.scheduleDateTime LIKE CONCAT(:today, '%') AND p.remark = :remark")
    int deletePlanes(@Param("today") String today, @Param("remark") String remark);

    // 2일전 날짜의 출발 완료 항공편 삭제
    void deleteBySearchDateAndRemark(String searchDate, String remark);

    Slice<Plane> findBySearchDate(String searchDate, Pageable pageable);
}

