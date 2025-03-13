package com.example.chat.chat.chat;

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
