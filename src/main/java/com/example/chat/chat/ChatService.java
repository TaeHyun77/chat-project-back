package com.example.chat.chat;

import com.example.chat.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final JwtUtil jwtUtil;

    public MessageResponseDto pushMessage(MessageRequestDto requestDto) {

        log.info("access : " + requestDto.getAccessToken());

        String token = requestDto.getAccessToken();

        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token");
        }

        token = token.substring(7);

        if (jwtUtil.isExpired(token)) {
            throw new IllegalArgumentException("Token expired");
        }

        String username = jwtUtil.getUsername(token);

        return MessageResponseDto.builder()
                .content(requestDto.getContent())
                .username(username)
                .timestamp(requestDto.getTimestamp())
                .build();
    }
}
