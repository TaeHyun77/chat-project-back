package com.example.chat.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageResponseDto {
    private String content;
    private String sessionId;

    @Builder
    public MessageResponseDto(String content, String sessionId) {
        this.content = content;
        this.sessionId = sessionId;
    }
}

