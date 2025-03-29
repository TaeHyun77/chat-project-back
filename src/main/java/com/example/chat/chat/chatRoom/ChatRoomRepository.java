package com.example.chat.chat.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT c FROM ChatRoom c JOIN FETCH c.member")
    List<ChatRoom> getChatRooms();

    // 특정 채팅방 조회
    ChatRoom findByChatRoomId(String chatRoomId);

}
