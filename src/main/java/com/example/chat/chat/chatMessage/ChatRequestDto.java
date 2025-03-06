package com.example.chat.chat.chatMessage;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ChatRequestDto {

    private ChatType chatType;

    private String content;

    private String accessToken;

    private LocalDateTime createdAt;

    private String username;

    private String roomId;
}
