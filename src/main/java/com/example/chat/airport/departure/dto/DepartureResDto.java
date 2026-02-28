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
                .t1Depart1(departure.getT1Depart1())
                .t1Depart2(departure.getT1Depart2())
                .t1Depart3(departure.getT1Depart3())
                .t1Depart4(departure.getT1Depart4())
                .t1Depart5(departure.getT1Depart5())
                .t1Depart6(departure.getT1Depart6())
                .t2Depart1(departure.getT2Depart1())
                .t2Depart2(departure.getT2Depart2())
                .build();
    }
}
