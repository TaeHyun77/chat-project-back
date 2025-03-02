package com.example.chat.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageResponseDto {
    private String content;
    private String username;
    private String name;
    private String email;
    private String timestamp;
    private String roomId;

    @Builder
    public MessageResponseDto(String content, String username, String name, String email, String timestamp, String roomId) {
        this.content = content;
        this.username = username;
        this.name = name;
        this.email = email;
        this.timestamp = timestamp;
        this.roomId = roomId;
    }
}

