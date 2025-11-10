package com.example.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Redis 설정 클래스
@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    // Redis와 연결을 담당하는 메서드
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        // 단일 Redis 서버 설정
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);

        // Redis 연결을 관리, Lettuce는 비동기 Redis 클라이언트로, Spring Boot에서 기본적으로 사용
        return new LettuceConnectionFactory(redisConfig);
    }

    // LocalDate, LocalDateTime 등의 날짜/시간 API를 직렬화할 수 있도록 지원하는 모듈을 등록하기 위함
    // 기본적으로 Jacson은 LocalDateTime을 인식하지 못하여 오류를 내거나 이상한 숫자로 바꾸는데 이 모듈을 등록하면 정상적인 형태로 변환해줌
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // 날짜를 숫자로 쓰지 않고, 문자열로 쓰게 만드는 설정 ( true가 기본 값 )

        return objectMapper;
    }

    // redis 설정 코드
    // 이를 통해 ObjectMapper로 변환해여 redis에 저장하지 않아도 되고, redisTemplate을 사용하여 객체를 바로 저장해도 변환이 이루어짐
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // redis와 연결
        template.setConnectionFactory(redisFactory);

        // GenericJackson2JsonRedisSerializer : redis에서 직렬화/역직렬화를 할 때 사용하는 도구
        // Jackson(ObjectMapper 기반)으로 Redis 데이터를 JSON 형태로 저장하고 읽을 수 있도록 도와주는 직렬화 도구
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setKeySerializer(new StringRedisSerializer()); // 키 직렬화
        template.setValueSerializer(serializer); // 값 직렬화

        return template;
    }
}