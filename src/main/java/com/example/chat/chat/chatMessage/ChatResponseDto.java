package com.example.chat.chat.chatMessage;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatResponseDto {
    private ChatType chatType;
    private String content;
    private String username;
    private String name;
    private String email;
    private String timestamp;
    private String roomId;

    @Builder
    public ChatResponseDto(ChatType chatType, String content, String username, String name, String email, String timestamp, String roomId) {
        this.chatType = chatType;
        this.content = content;
        this.username = username;
        this.name = name;
        this.email = email;
        this.timestamp = timestamp;
        this.roomId = roomId;
    }
}

