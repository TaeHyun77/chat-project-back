package com.example.chat.chat.chat.repository;

import com.example.chat.chat.chat.Chat;
import com.example.chat.chat.chat.dto.ChatResDto;

import java.util.List;

public interface ChatRepositoryCustom {

    // 특정 채팅방의 채팅 목록 조회
    List<Chat> getChatsByChatRoomId(String chatRoomId);

}
