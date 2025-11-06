package com.example.chat.chat.chatRoom.repository;

import com.example.chat.chat.chatRoom.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepositoryCustom {

    List<ChatRoom> getChatRooms();
    
    Optional<ChatRoom> findByChatRoomId(String chatRoomId);

    Optional<ChatRoom> findChatRoomWithMember(String chatRoomId);

}
