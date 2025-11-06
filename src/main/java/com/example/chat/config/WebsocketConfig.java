package com.example.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// WebSocket + STOMP 프로토콜을 설정하는 클래스
@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    // 클라이언트가 WebSocket 연결을 시도할 때 접속할 엔드포인트를 지정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000", "https://incheon-airport-info.site") // 접속을 허용할 도메인 지정
                .setAllowedOriginPatterns("*")
                .withSockJS(); // SockJS fallback을 활성화
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // 메세지를 구독하는 경로의 prefix
        registry.setApplicationDestinationPrefixes("/app"); // 클라이언트가 서버로 메시지를 보낼 때 사용하는 경로의 prefix
    }
}

/*
* 클라이언트가 /topic/test1 경로를 구독하면, 서버에서 /topic/planes 경로로 메시지를 보낼 때 수신할 수 있음
*
* 클라이언트에서 /app/test1 경로로 메시지를 전송하면, 서버의 @MessageMapping("/test1") 메서드가 이를 처리
* 즉, /app/** 경로로 보내는 메세지는 설정한 @MessageMapping에 의해 처리됨
* */