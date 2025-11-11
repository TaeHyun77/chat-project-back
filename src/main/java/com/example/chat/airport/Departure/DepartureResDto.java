package com.example.chat.airport.Departure;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

    @Builder
    public DepartureResDto(String date, String timeZone, Long t1Depart1, Long t1Depart2, Long t1Depart3, Long t1Depart4, Long t1Depart5, Long t1Depart6, Long t2Depart1, Long t2Depart2) {
        this.date = date;
        this.timeZone = timeZone;
        this.t1Depart1 = t1Depart1;
        this.t1Depart2 = t1Depart2;
        this.t1Depart3 = t1Depart3;
        this.t1Depart4 = t1Depart4;
        this.t1Depart5 = t1Depart5;
        this.t1Depart6 = t1Depart6;
        this.t2Depart1 = t2Depart1;
        this.t2Depart2 = t2Depart2;
    }

    // page 테스트
    public DepartureResDto(Departure departure) {
        this.date = departure.getDate();
        this.timeZone = departure.getTimeZone();
        this.t1Depart1 = departure.getT1Depart1();
        this.t1Depart2 = departure.getT1Depart2();
        this.t1Depart3 = departure.getT1Depart3();
        this.t1Depart4 = departure.getT1Depart4();
        this.t1Depart5 = departure.getT1Depart5();
        this.t1Depart6 = departure.getT1Depart6();
        this.t2Depart1 = departure.getT2Depart1();
        this.t2Depart2 = departure.getT2Depart2();
    }
}
