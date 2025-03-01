package com.example.chat.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    @MessageMapping("/chat") // 클라이언트에서 /app/chat으로 보내면 여기서 받음
    @SendTo("/topic/chat")
    public MessageResponseDto sendMessage(MessageRequestDto requestDto) {
        MessageResponseDto responseDto = new MessageResponseDto();
        responseDto.setContent(requestDto.getContent());

        return responseDto;
    }
}
