package com.example.chat.airport.departure.domain;

import com.example.chat.airport.departure.domain.T1GateStatus;
import com.example.chat.airport.departure.domain.T2GateStatus;
import com.example.chat.common.BaseTime;
import jakarta.persistence.*;
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

    @Embedded // 해당 값 타입을 엔티티의 컬럼으로 포함해서 매핑
    @AttributeOverrides({
            @AttributeOverride(name = "gate1", column = @Column(name = "t1_depart1")),
            @AttributeOverride(name = "gate2", column = @Column(name = "t1_depart2")),
            @AttributeOverride(name = "gate3", column = @Column(name = "t1_depart3")),
            @AttributeOverride(name = "gate4", column = @Column(name = "t1_depart4")),
            @AttributeOverride(name = "gate5", column = @Column(name = "t1_depart5")),
            @AttributeOverride(name = "gate6", column = @Column(name = "t1_depart6")),
    })
    private T1GateStatus t1Gates;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "gate1", column = @Column(name = "t2_depart1")),
            @AttributeOverride(name = "gate2", column = @Column(name = "t2_depart2")),
    })
    private T2GateStatus t2Gates;

    public void updateDeparture(T1GateStatus t1Gates, T2GateStatus t2Gates) {
        this.t1Gates = t1Gates;
        this.t2Gates = t2Gates;
    }
}
