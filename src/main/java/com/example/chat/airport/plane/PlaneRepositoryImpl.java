package com.example.chat.airport.plane;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PlaneRepositoryImpl implements PlaneRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Long deleteByScheduleDate(String today) {

        return queryFactory
                .delete(QPlane.plane)
                .where(QPlane.plane.scheduleDateTime.startsWith(today)
                        .and(QPlane.plane.remark.eq("출발")))
                .execute();
    }

    // 유지 날짜(오늘/내일/모레)에 해당하지 않는 출발 완료 항공편 일괄 삭제
    @Override
    public Long deleteOldDepartedPlanes(List<String> retainDates) {

        return queryFactory
                .delete(QPlane.plane)
                .where(QPlane.plane.searchDate.notIn(retainDates)
                        .and(QPlane.plane.remark.eq("출발")))
                .execute();
    }
}
