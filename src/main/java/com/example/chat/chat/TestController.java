package com.example.chat.chat;

import com.example.chat.chat.chatMessage.ChatRepository;
import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.chat.chatRoom.ChatRoomRepository;
import com.example.chat.member.Member;
import com.example.chat.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class TestController {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;

    @DeleteMapping("/delete")
    public void deleteEx() {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(6L);

        chatRoom.ifPresent(chatRoomRepository::delete);
    }

}
