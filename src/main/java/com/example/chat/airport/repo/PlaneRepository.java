package com.example.chat.airport.repo;

import com.example.chat.airport.entity.Plane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaneRepository extends JpaRepository<Plane, Long>, PlaneRepositoryCustom{

    /*
    @Modifying
    @Query("DELETE FROM Plane d WHERE d.scheduleDatetime LIKE :today% AND d.remark = '출발'")
    void deleteByScheduleDateStartsWith(@Param("today") String today);
    */

    Plane findByFlightIdAndScheduleDatetime(String flightId, String scheduleDatetime);

}
