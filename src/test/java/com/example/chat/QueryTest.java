package com.example.chat;

import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.chat.chatRoom.repository.ChatRoomRepository;
import com.example.chat.member.Member;
import com.example.chat.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@SpringBootTest
public class QueryTest {

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager entityManager;

    @Transactional
    @BeforeEach
    void initData() {
        for (int i = 1; i <= 8000; i++) {
            Member newMember = Member.builder().name("User" + i).build();
            memberRepository.save(newMember);

            ChatRoom chatRoom = ChatRoom.builder()
                    .chatRoomName("room " + i)
                    .build();

            chatRoom.setMember(newMember);
            chatRoomRepository.save(chatRoom);
        }
    }

    @Transactional
    @Test
    public void measureQueryTime() {

        entityManager.clear();

        long start = System.currentTimeMillis();

        // List<ChatRoom> result = chatRoomRepository.findAll();
        List<ChatRoom> result = chatRoomRepository.getChatRooms();

        for (ChatRoom chatRoom : result) {
            System.out.print(chatRoom.getMember().getName());
        }

        long end = System.currentTimeMillis();

        System.out.println("조회 결과 수: " + result.size());
        System.out.println("실행 시간(ms): " + (end - start));
    }
}
