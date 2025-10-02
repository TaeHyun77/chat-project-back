package com.example.chat.chat.chatRoom;

import com.example.chat.chat.chatRoom.dto.ChatRoomReqDto;
import com.example.chat.chat.chatRoom.dto.ChatRoomResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final SessionEventListener sessionEventListener;

    // 채팅방 생성
    @PostMapping("/api/chat/room")
    public void createRoom(@RequestBody ChatRoomReqDto dto) {
        chatRoomService.createChatRoom(dto);
    }

    // 채팅방 목록 조회
    @GetMapping("/api/chat/rooms")
    public List<ChatRoomResDto> getAllRooms() {
        return chatRoomService.selectAllChatRoom();
    }

    // 채팅방 작성자 정보 조회
    @GetMapping("/api/chatRoomInfo/{roomId}")
    public ChatRoomResDto chatRoomInfo(@PathVariable("roomId") String roomId) {

        return chatRoomService.chatRoomInfo(roomId);

    }

    // 채팅방 삭제
    @DeleteMapping("/api/delete/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable("roomId") String roomId) {
        return chatRoomService.deleteRoom(roomId);
    }

    // 현재 웹 소켓에 접속한 유저의 수 파악 ( 채팅방들에 존재하는 유저들의 총 합 )
    @MessageMapping("/chat/userCnt")
    public void sendAllUserCnt() {
        int userAllCnt = sessionEventListener.getAllUserCnt();

        messagingTemplate.convertAndSend("/topic/all/userCnt", userAllCnt);
    }

    // 특정 채팅방의 접속한 유저 수 파악
    @MessageMapping("/chatroom/userCnt")
    public void sendChatroomUserCnt() {

        String roomId = sessionEventListener.getChatRoomId();
        int chatroomUserCnt = sessionEventListener.getChatRoomUserCnt();

        messagingTemplate.convertAndSend("/topic/chatroom/userCnt/" + roomId, chatroomUserCnt);
    }
}
