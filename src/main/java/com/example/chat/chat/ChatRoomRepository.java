package com.example.chat.chat;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ChatRoomRepository {
    private final Map<String, ChatRoom> chatRoomMap = new LinkedHashMap<>();

    // 채팅방 생성
    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.builder()
                .name(name)
                .build();

        chatRoomMap.put(chatRoom.getChatRoomId(), chatRoom);
        return chatRoom;
    }

    // 모든 채팅방 조회
    public List<ChatRoom> findAllRooms() {
        return new ArrayList<>(chatRoomMap.values());
    }

    // 특정 채팅방 조회
    public ChatRoom findRoomById(String chatRoomId) {
        return chatRoomMap.get(chatRoomId);
    }
}

