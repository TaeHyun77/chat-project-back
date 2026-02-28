package com.example.chat.messaging.chat.dto;

import com.example.chat.messaging.chat.Chat;
import com.example.chat.messaging.chat.ChatType;
import com.example.chat.messaging.chatRoom.ChatRoom;
import com.example.chat.messaging.member.Member;
import lombok.Getter;
import lombok.Setter;

@Setter
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