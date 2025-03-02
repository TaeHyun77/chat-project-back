package com.example.chat.chat;

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

    // 채팅방 목록 조회
    @GetMapping("/chat/rooms")
    public List<ChatRoom> getAllRooms() {
        return chatRoomRepository.findAllRooms();
    }

    // 채팅방 생성
    @PostMapping("/chat/room")
    public ChatRoom createRoom(@RequestParam("name") String name) {
        return chatRoomRepository.createChatRoom(name);
    }

    // 특정 채팅방 조회
    @GetMapping("/chat/room/{roomId}")
    public ChatRoom getRoom(@PathVariable String roomId) {
        return chatRoomRepository.findRoomById(roomId);
    }
}
