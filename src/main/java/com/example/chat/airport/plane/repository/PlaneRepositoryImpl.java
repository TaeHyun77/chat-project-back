package com.example.chat.airport.plane.repository;

import com.example.chat.airport.plane.QPlane;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaneRepositoryImpl implements PlaneRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private static final String REMARK_DEPARTED = "출발";

    @Override
    public Long deleteByScheduleDate(String today) {

        return queryFactory
                .delete(QPlane.plane)
                .where(QPlane.plane.scheduleDateTime.startsWith(today)
                        .and(QPlane.plane.remark.eq(REMARK_DEPARTED)))
                .execute();
    }

    // 어제 날짜의 출발 완료 항공편 삭제
    @Override
    public Long deleteYesterdayDepartedPlanes(String yesterday) {

        return queryFactory
                .delete(QPlane.plane)
                .where(QPlane.plane.searchDate.eq(yesterday)
                        .and(QPlane.plane.remark.eq(REMARK_DEPARTED)))
                .execute();
    }
}
