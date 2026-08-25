package com.example.chat.airport.departure.vo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 제2여객터미널 출국장 게이트별 대기 인원 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class T2GateStatus {

    private Long gate1;
    private Long gate2;

    public long sum() {
        return gate1 + gate2;
    }

    public boolean isBusy(long threshold) {
        return sum() >= threshold;
    }
}
