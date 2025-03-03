package com.example.chat.chat.chatMessage;

import com.example.chat.chat.chatMessage.ChatService;
import com.example.chat.chat.chatMessage.MessageRequestDto;
import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.chat.chatRoom.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;

    private final ChatRoomRepository chatRoomRepository;

    @MessageMapping("/chat/message")
    public void sendMessage(MessageRequestDto requestDto) {

        chatService.pushMessage(requestDto);
    }
}
