package com.example.chat.airport.departure.dto;

import com.example.chat.airport.departure.Departure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartureResDto {

    private String date;

    private String timeZone;

    private Long t1Depart1;

    private Long t1Depart2;

    private Long t1Depart3;

    private Long t1Depart4;

    private Long t1Depart5;

    private Long t1Depart6;

    private Long t2Depart1;

    private Long t2Depart2;

    public static DepartureResDto from(Departure departure) {
        return DepartureResDto.builder()
                .date(departure.getDate())
                .timeZone(departure.getTimeZone())
                .t1Depart1(departure.getT1Gates().getGate1())
                .t1Depart2(departure.getT1Gates().getGate2())
                .t1Depart3(departure.getT1Gates().getGate3())
                .t1Depart4(departure.getT1Gates().getGate4())
                .t1Depart5(departure.getT1Gates().getGate5())
                .t1Depart6(departure.getT1Gates().getGate6())
                .t2Depart1(departure.getT2Gates().getGate1())
                .t2Depart2(departure.getT2Gates().getGate2())
                .build();
    }
}
