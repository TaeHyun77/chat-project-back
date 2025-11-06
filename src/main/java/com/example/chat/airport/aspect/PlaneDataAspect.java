package com.example.chat.airport.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component // Spring AOP는 스프링이 관리하는 빈에서만 작동하기에
@Aspect
public class PlaneDataAspect {

    @Around("execution(* com.example.chat.airport.AirportService.getPlane(..))")
    public Object loadingPlaneApi(ProceedingJoinPoint joinPoint) throws Throwable{

        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        log.info("항공편 Api 로딩 시간 : {}ms", executionTime);

        return proceed;
    }

    @Around("execution(* com.example.chat.airport.AirportService.getAllPlanes(..))")
    public Object searchingPlanes(ProceedingJoinPoint joinPoint) throws Throwable{

        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        log.info("항공편 데이터 조회 시간 : {}ms", executionTime);
        return proceed;
    }
}
