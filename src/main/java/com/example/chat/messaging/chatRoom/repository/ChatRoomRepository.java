package com.example.chat.messaging.chatRoom.repository;

import com.example.chat.messaging.chatRoom.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepositoryCustom {

    Optional<ChatRoom> findByChatRoomId(String chatRoomId);
}