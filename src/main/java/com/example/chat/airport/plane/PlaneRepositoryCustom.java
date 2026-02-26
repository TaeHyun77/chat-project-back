package com.example.chat.airport.plane;

import java.util.List;

public interface PlaneRepositoryCustom {

    Long deleteByScheduleDate(String today);

    // 유지 날짜(오늘/내일/모레)에 해당하지 않는 출발 완료 항공편 삭제
    Long deleteOldDepartedPlanes(List<String> retainDates);
}
