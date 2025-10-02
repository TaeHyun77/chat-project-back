package com.example.chat.chat.chatRoom.repository;

import com.example.chat.chat.chatRoom.ChatRoom;

import java.util.List;

public interface ChatRoomRepositoryCustom {

    List<ChatRoom> getChatRooms();
    
    ChatRoom findByChatRoomId(String chatRoomId);

    ChatRoom findChatRoomWithMember(String chatRoomId);

}
