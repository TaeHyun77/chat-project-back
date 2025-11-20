package com.example.chat;

import com.example.chat.airport.plane.PlaneService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PlaneServiceTest {

    private static final int RUN_COUNT = 10;

    @Autowired
    private PlaneService planeService;

    @Test
    @DisplayName("redis만 사용하는 것과 DB만 사용하는것의 속도 차이 테스트")
    void redisOrDbSpeedTest() {

        long totalTime = 0;

        for(int i = 0; i < RUN_COUNT; i++) {

            long startTime = System.currentTimeMillis();
            planeService.getPlaneData();
            long endTime = System.currentTimeMillis();

            long duration = endTime - startTime;
            System.out.printf("%d회차 소요 시간: %d ms\n", i + 1, duration);
            totalTime += duration;
        }

        long averageTime = totalTime / RUN_COUNT;
        System.out.println("Redis 사용 10회 평균 소요 시간 : " + averageTime);
    }
}
