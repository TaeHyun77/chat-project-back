package com.example.chat.airport.departure.domain;

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
    private long gate1;
    private long gate2;

    public long totalWaiting() {
        return gate1 + gate2;
    }

    public boolean isCongested(long threshold) {
        return totalWaiting() >= threshold;
    }
}
