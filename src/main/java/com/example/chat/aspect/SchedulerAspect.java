package com.example.chat.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class SchedulerAspect {

    @Around("execution(* com.example.chat.airport.AirportScheduler.sync*(..))")
    public Object measureSchedulerSync(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        log.info("[스케줄러] {} 완료 - 소요 시간: {}ms", methodName, executionTime);

        return proceed;
    }
}
