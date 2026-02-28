package com.example.chat.airport.plane.repository;

public interface PlaneRepositoryCustom {

    Long deleteByScheduleDate(String today);

    // 어제 날짜의 출발 완료 항공편 삭제
    Long deleteYesterdayDepartedPlanes(String yesterday);
}
