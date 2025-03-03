package com.example.chat.chat.chatMessage;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageResponseDto {
    private MessageType messageType;
    private String content;
    private String username;
    private String name;
    private String email;
    private String timestamp;
    private String roomId;

    @Builder
    public MessageResponseDto(MessageType messageType, String content, String username, String name, String email, String timestamp, String roomId) {
        this.messageType = messageType;
        this.content = content;
        this.username = username;
        this.name = name;
        this.email = email;
        this.timestamp = timestamp;
        this.roomId = roomId;
    }
}

