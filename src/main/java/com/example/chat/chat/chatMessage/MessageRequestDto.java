package com.example.chat.chat.chatMessage;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageRequestDto {

    private MessageType messageType;

    private String content;

    private String accessToken;

    private String timestamp;

    private String roomId;
}
