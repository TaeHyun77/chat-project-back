package com.example.chat.chat.chat.repository;

import com.example.chat.chat.chat.Chat;

import java.util.List;

public interface ChatRepositoryCustom {

    List<Chat> getChats(String chatRoomId);

}
