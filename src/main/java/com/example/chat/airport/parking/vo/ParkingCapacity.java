package com.example.chat.airport.parking.vo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 주차 구역의 현재 주차 대수와 총 주차 면수 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ParkingCapacity {
    private int parking;      // 현재 주차 대수
    private int parkingarea;  // 총 주차 면수 (0이면 미운영)

    // 가용률 계산 (0~100%, 미운영 시 -1)
    public int getAvailableRate() {
        if (parkingarea == 0) return -1;
        return (parkingarea - parking) * 100 / parkingarea;
    }
}
