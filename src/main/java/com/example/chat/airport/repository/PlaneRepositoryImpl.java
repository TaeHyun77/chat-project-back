package com.example.chat.airport.repository;

import com.example.chat.airport.entity.QPlane;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaneRepositoryImpl implements PlaneRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public long deleteByScheduleDateStartsWith(String today) {

        return queryFactory
                .delete(QPlane.plane)
                .where(QPlane.plane.scheduleDatetime.startsWith(today)
                        .and(QPlane.plane.remark.eq("출발")))
                .execute();
    }
}
