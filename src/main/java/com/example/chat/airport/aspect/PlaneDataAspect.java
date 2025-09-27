package com.example.chat.airport.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class PlaneDataAspect {

    @Around("execution(* com.example.chat.airport.AirService.getPlane(..))")
    public Object aspectPlane(ProceedingJoinPoint joinPoint) throws Throwable{

        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        log.info("항공편 Api 로딩 시간 : {}ms", executionTime);
        return proceed;
    }
}
