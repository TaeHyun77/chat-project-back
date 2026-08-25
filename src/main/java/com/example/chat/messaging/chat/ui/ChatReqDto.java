package com.example.chat.messaging.chat.ui;

import com.example.chat.messaging.chat.domain.Chat;
import com.example.chat.messaging.chat.domain.ChatType;
import com.example.chat.messaging.chatRoom.domain.ChatRoom;
import com.example.chat.member.domain.Member;
import lombok.Getter;

@Getter
public class ChatReqDto {

    private ChatType chatType;

    private String content;

    private String chatroomId;

    public Chat toEntity(ChatRoom chatRoom, Member member) {
        return Chat.builder()
                .chatType(chatType)
                .content(content)
                .chatRoom(chatRoom)
                .member(member)
                .build();
    }
}