package com.example.chat.messaging.chatRoom.domain;

import com.example.chat.messaging.chatRoom.domain.ChatRoom;

import java.util.List;

public interface ChatRoomRepositoryCustom {

    // 채팅방 목록 조회
    List<ChatRoom> getChatRooms();

    // 특정 사용자의 채팅방 목록 조회
    List<ChatRoom> findChatroomsByMemberId(Long memberId);
}
