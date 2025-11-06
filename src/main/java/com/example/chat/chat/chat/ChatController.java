package com.example.chat.chat.chat;

import com.example.chat.chat.chat.dto.ChatReqDto;
import com.example.chat.chat.chat.dto.ChatResDto;
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

    // 클라이언트가 /app/chat/message 경로로 보낸 메시지는 이 @MessageMapping 메서드에서 처리됨
    // 즉, 모든 채팅 메시지는 클라이언트로부터 /app/chat/message 경로로 발송되며, 해당 메서드를 통해 처리된다는 것
    @MessageMapping("/chat/message")
    public void sendMessage(ChatReqDto requestDto) {

        chatService.pushMessage(requestDto);

    }

    // 특정 채팅방의 채팅 리스트 반환
    @GetMapping("/api/chats/{chatRoomId}")
    public List<ChatResDto> getAllChats(@PathVariable("chatRoomId") String chatRoomId) {

        return chatService.getChatListByChatroom(chatRoomId);

    }
}
