package com.example.chat.airport.departure;

import com.example.chat.common.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Departure extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public void updateDeparture(long t1Depart1, long t1Depart2, long t1Depart3, long t1Depart4,
                                long t1Depart5, long t1Depart6, long t2Depart1, long t2Depart2) {
        this.t1Depart1 = t1Depart1;
        this.t1Depart2 = t1Depart2;
        this.t1Depart3 = t1Depart3;
        this.t1Depart4 = t1Depart4;
        this.t1Depart5 = t1Depart5;
        this.t1Depart6 = t1Depart6;
        this.t2Depart1 = t2Depart1;
        this.t2Depart2 = t2Depart2;
    }
}