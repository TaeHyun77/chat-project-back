package com.example.chat.messaging.chat.domain;

import com.example.chat.messaging.chat.domain.Chat;

import java.util.List;

public interface ChatRepositoryCustom {

    // 특정 채팅방의 채팅 목록 조회
    List<Chat> getChatsByChatRoomId(String chatRoomId);

}
