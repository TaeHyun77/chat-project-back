package com.example.chat.chat.chatRoom;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    // 특정 채팅방 조회
//    @GetMapping("/chat/room/{roomId}")
//    public ChatRoom getRoom(@PathVariable String roomId) {
//        return chatRoomRepository.findRoomById(roomId);
//    }

}
