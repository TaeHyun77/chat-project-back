package com.example.chat.airport.Departure;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DepartureResDto {

    private String date;

    private String timeZone;

    private Long t1Depart12;

    private Long t1Depart3;

    private Long t1Depart4;

    private Long t1Depart56;

    private Long t1DepartSum;

    private Long t2Depart1;

    private Long t2Depart2;

    private Long t2DepartSum;

    @Builder
    public DepartureResDto(String date, String timeZone, Long t1Depart12, Long t1Depart3, Long t1Depart4, Long t1Depart56, Long t1DepartSum, Long t2Depart1, Long t2Depart2, Long t2DepartSum) {
        this.date = date;
        this.timeZone = timeZone;
        this.t1Depart12 = t1Depart12;
        this.t1Depart3 = t1Depart3;
        this.t1Depart4 = t1Depart4;
        this.t1Depart56 = t1Depart56;
        this.t1DepartSum = t1DepartSum;
        this.t2Depart1 = t2Depart1;
        this.t2Depart2 = t2Depart2;
        this.t2DepartSum = t2DepartSum;
    }

    // page 테스트
    public DepartureResDto(Departure departure) {
        this.date = departure.getDate();
        this.timeZone = departure.getTimeZone();
        this.t1Depart12 = departure.getT1Depart12();
        this.t1Depart3 = departure.getT1Depart3();
        this.t1Depart4 = departure.getT1Depart4();
        this.t1Depart56 = departure.getT1Depart56();
        this.t1DepartSum = departure.getT1DepartSum();
        this.t2Depart1 = departure.getT2Depart1();
        this.t2Depart2 = departure.getT2Depart2();
        this.t2DepartSum = departure.getT2DepartSum();
    }
}
