package com.example.chat.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.chat.airport.plane.Plane;

import java.util.List;
import java.util.Optional;

public interface PlaneSubscriptionRepository extends JpaRepository<PlaneSubscription, Long> {

    Optional<PlaneSubscription> findByMemberAndPlane(Member member, Plane plane);

    List<PlaneSubscription> findByMember(Member member);

    void deleteByMemberAndPlane(Member member, Plane plane);

    @Query("SELECT ps.member FROM PlaneSubscription ps WHERE ps.plane.flightId = :flightId")
    List<Member> findMembersByPlaneFlightId(@Param("flightId") String flightId);

    @Query("SELECT DISTINCT ps.member FROM PlaneSubscription ps " +
            "WHERE ps.member.congestionAlertEnabled = true " +
            "AND ps.plane.scheduleDateTime BETWEEN :start AND :end")
    List<Member> findCongestionAlertEnabledMembersWithImminentFlights(
            @Param("start") String start, @Param("end") String end);
}
