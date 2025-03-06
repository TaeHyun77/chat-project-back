package com.example.chat.chat.chatRoom;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅방 생성
    @PostMapping("/chat/room")
    public void createRoom(@RequestBody ChatRoomRequestDto dto) {
        chatRoomService.createChatRoom(dto);
    }

    // 채팅방 목록 조회
    @GetMapping("/chat/rooms")
    public List<ChatRoom> getAllRooms() {
        return chatRoomService.selectAllChatRoom();
    }


}
