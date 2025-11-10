package com.example.chat.airport.plane;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaneRepository extends JpaRepository<Plane, Long>, PlaneRepositoryCustom{

    /*
    @Modifying
    @Query("DELETE FROM Plane d WHERE d.scheduleDatetime LIKE :today% AND d.remark = '출발'")
    void deleteByScheduleDateStartsWith(@Param("today") String today);
    */

    List<Plane> findBySearchDate(String searchDate);

    Plane findByFlightIdAndScheduleDateTime(String flightId, String scheduleDateTime);
}

