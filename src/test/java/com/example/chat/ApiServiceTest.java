package com.example.chat;

import com.example.chat.airport.ApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApiServiceTest {

    @Autowired
    private ApiService apiService;

    /*
     * getApiPlane() 실행 시간 측정 (10회 평균)
     *
     * 주의: 1회차는 신규 INSERT 위주, 2~10회차는 dirty checking UPDATE 위주로 동작하므로
     * 1회차 시간이 이후 회차보다 길게 측정될 수 있음
     * */
    @Test
    public void getApiPlane_실행시간_평균_측정() {
        int runs = 10;
        long[] times = new long[runs];

        for (int i = 0; i < runs; i++) {
            long start = System.currentTimeMillis();
            apiService.getApiPlane();
            long end = System.currentTimeMillis();

            times[i] = end - start;
            System.out.printf("%2d회차 실행 시간: %d ms%n", i + 1, times[i]);
        }

        long total = 0;
        long min = times[0];
        long max = times[0];

        for (long time : times) {
            total += time;
            if (time < min) min = time;
            if (time > max) max = time;
        }

        System.out.println("-----------------------------");
        System.out.printf("평균 실행 시간: %d ms%n", total / runs);
        System.out.printf("최솟값:        %d ms%n", min);
        System.out.printf("최댓값:        %d ms%n", max);
    }
}
