package com.example.chat.airport.transit.arexTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 공항철도 ~ 각 체크인 카운터까지의 소요 시간
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "arex_transit_time", uniqueConstraints = @UniqueConstraint(columnNames = {"terminal", "stationName", "checkInCounter"}))
public class ArexTransitTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 터미널 (T1 / T2)
    private String terminal;

    // 공항철도역명 (예: "인천공항1터미널역")
    private String stationName;

    // 체크인 카운터 (A~N)
    private String checkInCounter;

    // 소요시간 (초)
    private int travelSeconds;
}
