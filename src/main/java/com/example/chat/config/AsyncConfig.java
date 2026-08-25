package com.example.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 이벤트 리스너의 비동기 실행을 위한 스레드 풀 설정.
 * 기본 SimpleAsyncTaskExecutor는 요청마다 스레드를 무제한 생성하므로,
 * 경계가 있는 ThreadPoolTaskExecutor를 명시적으로 사용한다.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    public static final String EVENT_EXECUTOR = "eventTaskExecutor";

    @Bean(EVENT_EXECUTOR)
    public Executor eventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("event-async-");
        executor.initialize();
        return executor;
    }
}
