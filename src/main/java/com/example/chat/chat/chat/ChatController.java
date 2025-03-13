package com.example.chat.chat.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;

    private final ChatRepository chatRepository;

    @MessageMapping("/chat/message")
    public void sendMessage(ChatRequestDto requestDto) {

        chatService.pushMessage(requestDto);

    }

    // 특정 채팅방의 채팅 내역 조회
    @GetMapping("/chat/room/{roomId}/messages")
    public List<Chat> getChatHistory(@PathVariable("roomId") String roomId) {

        return chatRepository.findByChatRoomId(roomId);

    }
}
