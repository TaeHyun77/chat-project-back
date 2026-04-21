package com.example.chat.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.chat.airport.plane.Plane;
import java.util.List;

public interface PlaneSubscriptionRepository extends JpaRepository<PlaneSubscription, Long> {

    List<PlaneSubscription> findByMember(Member member);

    // memberId, planeId로 특정 PlaneSubscription 데이터 삭제
    @Modifying
    @Query("DELETE FROM PlaneSubscription ps WHERE ps.member.id = :memberId AND ps.plane.id = :planeId")
    void deleteByMemberIdAndPlaneId(@Param("memberId") Long memberId, @Param("planeId") Long planeId);

    // 특정 member, plane인 PlaneSubscription의 존재 여부
    boolean existsByMemberAndPlane(Member member, Plane plane);

    // 특정 flightId인 항공편을 구독한 사용자 목록 조회
    @Query("SELECT ps.member FROM PlaneSubscription ps WHERE ps.plane.flightId = :flightId")
    List<Member> findMembersByPlaneFlightId(@Param("flightId") String flightId);

    // congestionAlertEnabled가 true ( 혼잡도 알림을 허용한 ) 이고 항공편 출발 시각이 start ~ end 사이인 항공편을 구독한 사용자 목록 조회
    @Query("SELECT DISTINCT ps.member FROM PlaneSubscription ps " +
            "WHERE ps.member.congestionAlertEnabled = true " +
            "AND ps.plane.scheduleDateTime BETWEEN :start AND :end")
    List<Member> findCongestionAlertEnabledMembersWithImminentFlights(
            @Param("start") String start,
            @Param("end") String end
    );
}