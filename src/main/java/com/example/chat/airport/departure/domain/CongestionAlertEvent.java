package com.example.chat.airport.departure.domain;

import com.example.chat.airport.departure.domain.T1GateStatus;
import com.example.chat.airport.departure.domain.T2GateStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 출국장 혼잡도가 임계값을 넘어 혼잡 상태로 진입했을 때 알림 메일 발송을 요청하는 도메인 이벤트
// busyTerminal : 혼잡 상태에 진입한 터미널 ID ("T1" 또는 "T2") — 구독자 필터링에 사용
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CongestionAlertEvent {
    private String date;
    private String timeZone;
    private String busyTerminal;
    private T1GateStatus t1Gates;
    private T2GateStatus t2Gates;
}