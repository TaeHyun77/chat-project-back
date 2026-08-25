package com.example.chat.airport.parking;

import com.example.chat.airport.parking.vo.ParkingCapacity;
import com.example.chat.common.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 실시간 주차장 정보
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "parking")
public class Parking extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주차구역 문자열
    @Column(unique = true)
    private String floor;

    @Embedded
    private ParkingCapacity capacity;

    // 업데이트 시간
    private String datetm;

    public void updateParking(ParkingCapacity capacity, String datetm) {
        this.capacity = capacity;
        this.datetm = datetm;
    }
}
