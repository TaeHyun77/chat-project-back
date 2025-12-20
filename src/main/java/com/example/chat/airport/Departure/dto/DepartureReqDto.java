package com.example.chat.airport.Departure.dto;

import com.example.chat.airport.Departure.Departure;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
public class DepartureReqDto {

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
}
