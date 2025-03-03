package com.example.chat.chat.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 특정 채팅방 조회
    ChatRoom findByChatRoomId(String chatRoomId);
}
