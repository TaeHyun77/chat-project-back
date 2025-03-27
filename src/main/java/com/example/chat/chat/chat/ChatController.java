package com.example.chat.chat.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;

    private final ChatRepository chatRepository;

    // 특정 채팅방의 채팅 내역 조회
    @GetMapping("/api/chat/room/{roomId}/messages")
    public List<Chat> getChatHistory(@PathVariable("roomId") String roomId) {

        return chatService.getChatHistory(roomId);

    }

    // STOMP를 통해 클라이언트에서 보낸 메시지를 받아 서비스 로직에서 구독한 경로로 다시 반환 해주는 역할
    @MessageMapping("/chat/message")
    public void sendMessage(ChatRequestDto requestDto) {

        chatService.pushMessage(requestDto);

    }
}
