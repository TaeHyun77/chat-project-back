package com.example.chat.airport.Departure.repository;

import com.example.chat.ReadOnlyTransaction;
import com.example.chat.airport.Departure.Departure;
import com.example.chat.airport.Departure.QDeparture;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
class DepartureCustomRepositoryImpl implements DepartureCustomRepository {
    private final JPAQueryFactory queryFactory;

    // MySQL set-option 쿼리 방지를 위해 조회 시 트랜잭션 사용 안하기 위함
    @ReadOnlyTransaction
    @Override
    public List<Departure> findAllCustom() {
        QDeparture departure = QDeparture.departure;

        return queryFactory
                .selectFrom(departure)
                .fetch();
    }
}
