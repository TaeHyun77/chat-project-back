package com.example.chat.chat.chatMessage;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatRequestDto {

    private ChatType chatType;

    private String content;

    private String accessToken;

    private String timestamp;

    private String roomId;
}
