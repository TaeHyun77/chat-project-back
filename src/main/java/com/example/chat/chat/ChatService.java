package com.example.chat.chat;

import org.springframework.stereotype.Service;

@Service
public class ChatService {
    public MessageResponseDto pushMessage(MessageRequestDto requestDto, String sessionId) {

        return MessageResponseDto.builder()
                .content(requestDto.getContent())
                .sessionId(sessionId)
                .build();
    }
}
