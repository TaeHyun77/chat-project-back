package com.example.chat.messaging.chat;

import com.example.chat.messaging.chat.dto.ChatReqDto;
import com.example.chat.messaging.chat.dto.ChatResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;

    // 클라이언트가 /app/chat/message 경로로 보낸 메시지는 이 @MessageMapping 메서드에서 처리됨
    @MessageMapping("/chat/message")
    public void pushChat(ChatReqDto dto, Principal principal) {
        chatService.receiveChat(dto, principal.getName());
    }

    // 특정 채팅방의 채팅 리스트 반환
    @GetMapping("/api/chats/{chatRoomId}")
    public List<ChatResDto> getAllChats(@PathVariable("chatRoomId") String chatRoomId) {
        return chatService.getChatListByChatroom(chatRoomId);
    }
}
