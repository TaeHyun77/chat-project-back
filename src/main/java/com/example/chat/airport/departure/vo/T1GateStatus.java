package com.example.chat.airport.departure.vo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 제1여객터미널 출국장 게이트별 대기 인원
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class T1GateStatus {
    private Long gate1;
    private Long gate2;
    private Long gate3;
    private Long gate4;
    private Long gate5;
    private Long gate6;

    public long sum() {
        return gate1 + gate2 + gate3 + gate4 + gate5 + gate6;
    }

    public boolean isBusy(long threshold) {
        return sum() >= threshold;
    }
}
