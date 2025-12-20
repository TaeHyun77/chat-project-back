package com.example.chat.chat.chatRoom.repository;

import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.chat.chatRoom.dto.ChatRoomResDto;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepositoryCustom {

    // 채팅방 목록 조회
    List<ChatRoom> getChatRooms();

    // 특정 사용자의 채팅방 목록 조회
    List<ChatRoomResDto> findChatroomsByMemberId(Long memberId);
}
