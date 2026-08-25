package com.example.chat.airport.departure.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 제1여객터미널 출국장 게이트별 대기 인원
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable // 다른 엔티티에 포함될 수 있는 값 타입을 정의
public class T1GateStatus {
    private long gate1;
    private long gate2;
    private long gate3;
    private long gate4;
    private long gate5;
    private long gate6;

    public long totalWaiting() {
        return gate1 + gate2 + gate3 + gate4 + gate5 + gate6;
    }

    public boolean isCongested(long threshold) {
        return totalWaiting() >= threshold;
    }
}
