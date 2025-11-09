package com.example.chat.airport.Departure;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class DepartureDto {

    private String date;

    private String timeZone;

    private Long t1Depart1;

    private Long t1Depart2;

    private Long t1Depart3;

    private Long t1Depart4;

    private Long t1Depart5;

    private Long t1Depart6;

    private Long t1DepartSum;

    private Long t2Depart1;

    private Long t2Depart2;

    private Long t2DepartSum;

    @Builder
    public DepartureDto(String date, String timeZone, Long t1Depart1, Long t1Depart2, Long t1Depart3, Long t1Depart4, Long t1Depart5, Long t1Depart6,  Long t1DepartSum, Long t2Depart1, Long t2Depart2, Long t2DepartSum) {
        this.date = date;
        this.timeZone = timeZone;
        this.t1Depart1 = t1Depart1;
        this.t1Depart2 = t1Depart2;
        this.t1Depart3 = t1Depart3;
        this.t1Depart4 = t1Depart4;
        this.t1Depart5 = t1Depart5;
        this.t1Depart6 = t1Depart6;
        this.t1DepartSum = t1DepartSum;
        this.t2Depart1 = t2Depart1;
        this.t2Depart2 = t2Depart2;
        this.t2DepartSum = t2DepartSum;
    }

    public Departure toDepart() {
        return Departure.builder()
                .date(date)
                .timeZone(timeZone)
                .t1Depart1(t1Depart1)
                .t1Depart2(t1Depart2)
                .t1Depart3(t1Depart3)
                .t1Depart4(t1Depart4)
                .t1Depart5(t1Depart5)
                .t1Depart6(t1Depart6)
                .t1DepartSum(t1DepartSum)
                .t2Depart1(t2Depart1)
                .t2Depart2(t2Depart2)
                .t2DepartSum(t2DepartSum)
                .build();
    }
}
