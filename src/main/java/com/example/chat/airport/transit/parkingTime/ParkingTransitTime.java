package com.example.chat.airport.transit.parkingTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주차 구역 ~ 체크인 카운터 소요 시간
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "parking_transit_time",
        uniqueConstraints = @UniqueConstraint(columnNames = {"terminal", "parkingName", "zone", "checkInCounter"}))
public class ParkingTransitTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 터미널 (T1 / T2)
    private String terminal;

    // 주차장명 (예: "단기주차장지하2층")
    private String parkingName;

    // 구역 (예: "A구역", "서편")
    private String zone;

    // 체크인 카운터 (A~N)
    private String checkInCounter;

    // 소요시간 (초)
    private int travelSeconds;
}
