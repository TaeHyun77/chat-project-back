package com.example.chat.airport.plane;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaneRepositoryImpl implements PlaneRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public long deleteByScheduleDateStartsWith(String today) {

        return queryFactory
                .delete(QPlane.plane)
                .where(QPlane.plane.scheduleDateTime.startsWith(today)
                        .and(QPlane.plane.remark.eq("출발")))
                .execute();
    }
}
