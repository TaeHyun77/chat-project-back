package com.example.chat.airport.Departure;

import com.example.chat.config.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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

    @Builder
    public Departure(String date, String timeZone, Long t1Depart1, Long t1Depart2, Long t1Depart3, Long t1Depart4, Long t1Depart5, Long t1Depart6, Long t2Depart1, Long t2Depart2) {
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

    public void updateDeparture(Departure editDeparture) {
        this.t1Depart1 = editDeparture.t1Depart1;
        this.t1Depart2 = editDeparture.t1Depart2;
        this.t1Depart3 = editDeparture.t1Depart3;
        this.t1Depart4 = editDeparture.t1Depart4;
        this.t1Depart5 = editDeparture.t1Depart5;
        this.t1Depart6 = editDeparture.t1Depart6;
        this.t2Depart1 = editDeparture.t2Depart1;
        this.t2Depart2 = editDeparture.t2Depart2;
    }
}