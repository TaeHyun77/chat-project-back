package com.example.chat.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageResponseDto {
    private String content;
    private String username;
    private String timestamp;

    @Builder
    public MessageResponseDto(String content, String username, String timestamp) {
        this.content = content;
        this.username = username;
        this.timestamp = timestamp;
    }
}

