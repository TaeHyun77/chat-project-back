package com.example.chat.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;

    // client "/app/chat" 경로로 보내면 여기서 받음
    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public MessageResponseDto sendMessage(MessageRequestDto requestDto) {

        return chatService.pushMessage(requestDto);
    }
}
