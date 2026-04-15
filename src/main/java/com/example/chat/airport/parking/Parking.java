package com.example.chat.airport.parking;

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

    // 주차구역 문자열 (예: "T1 장기 P1 주차장")
    @Column(unique = true)
    private String floor;

    // 현재 주차대수
    private int parking;

    // 총 주차면수 (0이면 미운영)
    private int parkingarea;

    // 업데이트 시간 (ex. "20211103162024.804")
    private String datetm;

    public void updateParking(int parking, int parkingarea, String datetm) {
        this.parking = parking;
        this.parkingarea = parkingarea;
        this.datetm = datetm;
    }

    // 가용률 계산 (0~100%, 미운영 시 -1)
    public int getAvailableRate() {
        if (parkingarea == 0) return -1;
        return (parkingarea - parking) * 100 / parkingarea;
    }
}
