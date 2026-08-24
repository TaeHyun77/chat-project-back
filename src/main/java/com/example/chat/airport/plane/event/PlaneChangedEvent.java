package com.example.chat.airport.plane.event;

import com.example.chat.airport.plane.Plane;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 항공편 정보가 의미 있게 변경되었을 때 구독자 알림 메일 발송을 요청하는 도메인 이벤트.
 * 커밋 이후 리스너에서 메일이 발송된다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaneChangedEvent {

    private String flightId;
    private String airLine;
    private String airport;
    private String scheduleDateTime;

    // 변경 전 값
    private String prevRemark;
    private String prevEstimatedDateTime;
    private String prevGatenumber;

    // 변경 후 값
    private String newRemark;
    private String newEstimatedDateTime;
    private String newGatenumber;

    public static PlaneChangedEvent from(Plane plane,
                                         String newRemark,
                                         String newEstimatedDateTime,
                                         String newGatenumber) {
        return PlaneChangedEvent.builder()
                .flightId(plane.getFlightId())
                .airLine(plane.getAirLine())
                .airport(plane.getAirport())
                .scheduleDateTime(plane.getScheduleDateTime())
                .prevRemark(plane.getRemark())
                .prevEstimatedDateTime(plane.getEstimatedDateTime())
                .prevGatenumber(plane.getGatenumber())
                .newRemark(newRemark)
                .newEstimatedDateTime(newEstimatedDateTime)
                .newGatenumber(newGatenumber)
                .build();
    }
}
