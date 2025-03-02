package com.example.chat.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Setter
@Getter
public class MessageRequestDto {

    private MessageType messageType;

    private String content;

    private String accessToken;

    private String timestamp;

    private String roomId;
}
