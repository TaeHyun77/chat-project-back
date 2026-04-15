package com.example.chat.airport.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CongestionMessage {

    private String date;
    private String timeZone;

    private long t1Depart1;
    private long t1Depart2;
    private long t1Depart3;
    private long t1Depart4;
    private long t1Depart5;
    private long t1Depart6;

    private long t2Depart1;
    private long t2Depart2;

    private long prevT1Sum;
    private long newT1Sum;
    private long prevT2Sum;
    private long newT2Sum;
}
