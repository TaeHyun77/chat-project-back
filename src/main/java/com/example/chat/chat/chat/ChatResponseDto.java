package com.example.chat.chat.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ChatResponseDto {
    private ChatType chatType;
    private String content;
    private String username;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private String roomId;

    @Builder
    public ChatResponseDto(ChatType chatType, String content, String username, String name, String email, LocalDateTime createdAt, String roomId) {
        this.chatType = chatType;
        this.content = content;
        this.username = username;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.roomId = roomId;
    }
}

