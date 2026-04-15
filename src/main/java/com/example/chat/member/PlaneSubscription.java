package com.example.chat.member;

import com.example.chat.airport.plane.Plane;
import com.example.chat.common.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "plane_subscription",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "plane_id"}))
public class PlaneSubscription extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plane_id", nullable = false)
    private Plane plane;

    @Builder
    public PlaneSubscription(Member member, Plane plane) {
        this.member = member;
        this.plane = plane;
    }
}
