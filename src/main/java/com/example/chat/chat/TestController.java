package com.example.chat.chat;

import com.example.chat.chat.chatMessage.ChatRepository;
import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.chat.chatRoom.ChatRoomRepository;
import com.example.chat.member.Member;
import com.example.chat.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class TestController {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;

    @DeleteMapping("/delete")
    public void deleteEx() {

        chatRoomRepository.deleteAll();

    }

    private final SimpMessageSendingOperations messagingTemplate;

    @GetMapping("/send")
    public ResponseEntity<String> sendTestMessage() {
        int testUserCount = 100;

        System.out.println("[TEST] 테스트 메시지 전송: " + testUserCount);

        messagingTemplate.convertAndSend("/topic/All/number", testUserCount);

        return ResponseEntity.ok("테스트 메시지 전송 완료: " + testUserCount);
    }

}
