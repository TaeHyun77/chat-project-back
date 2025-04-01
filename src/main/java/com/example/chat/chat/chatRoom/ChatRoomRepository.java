package com.example.chat.chat.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepositoryCustom {

    /* JPQL
    @Query("SELECT c FROM ChatRoom c JOIN FETCH c.member")
    List<ChatRoom> getChatRooms();

    @Query("SELECT DISTINCT c FROM ChatRoom c LEFT JOIN FETCH c.member LEFT JOIN FETCH c.chats LEFT JOIN FETCH c.chats.member WHERE c.chatRoomId = :chatRoomId")
    ChatRoom findChatRoomWithChatsAndMember(@Param("chatRoomId") String chatRoomId);
    */
}
