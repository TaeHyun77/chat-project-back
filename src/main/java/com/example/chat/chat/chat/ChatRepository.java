package com.example.chat.chat.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // JPQL
    @Query("SELECT c FROM Chat c WHERE c.chatRoom.chatRoomId = :chatRoomId")
    List<Chat> findByChatRoomId(@Param("chatRoomId") String chatRoomId);

}
