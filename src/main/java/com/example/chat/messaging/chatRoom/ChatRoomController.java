package com.example.chat.messaging.chatRoom;

import com.example.chat.auth.oauth.CustomOAuth2User;
import com.example.chat.messaging.chatRoom.dto.ChatRoomReqDto;
import com.example.chat.messaging.chatRoom.dto.ChatRoomResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public void createChatroom(@RequestBody ChatRoomReqDto dto) {
        chatRoomService.createChatRoom(dto);
    }

    // 채팅방 삭제 (방 생성자만 가능)
    @DeleteMapping("/api/delete/{chatroomId}")
    public ResponseEntity<Void> deleteChatroom(
            @PathVariable("chatroomId") String chatroomId,
            @AuthenticationPrincipal CustomOAuth2User user) {
        chatRoomService.deleteChatroom(chatroomId, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    // 모든 채팅방 조회
    @GetMapping("/api/chat/rooms")
    public List<ChatRoomResDto> getAllChatroom() {
        return chatRoomService.getAllChatroom();
    }

    // 특정 채팅방 조회
    @GetMapping("/api/chatRoomInfo/{chatroomId}")
    public ChatRoomResDto getChatroomInfo(@PathVariable("chatroomId") String chatroomId) {
        return chatRoomService.getChatroomInfo(chatroomId);
    }

    // 현재 웹 소켓에 접속한 유저의 수 파악 ( 채팅방들에 존재하는 유저들의 총 합 )
    @MessageMapping("/chat/userCnt")
    public void sendAllUserCnt() {
        int userAllCnt = sessionEventListener.getAllUserCnt();
        messagingTemplate.convertAndSend("/topic/all/userCnt", userAllCnt);
    }

    // 특정 채팅방에 접속한 유저 수 파악
    // 클라이언트는 /app/chatroom/{roomId}/userCnt 경로로 요청
    @MessageMapping("/chatroom/{roomId}/userCnt")
    public void sendChatroomUserCnt(@DestinationVariable String roomId) {
        int chatroomUserCnt = sessionEventListener.getChatRoomUserCnt(roomId);
        messagingTemplate.convertAndSend("/topic/chatroom/userCnt/" + roomId, chatroomUserCnt);
    }
}
