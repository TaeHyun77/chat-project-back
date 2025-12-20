package com.example.chat.chat.chat.dto;

import com.example.chat.chat.chat.Chat;
import com.example.chat.chat.chat.ChatType;
import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.member.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ChatReqDto {

    private ChatType chatType;

    private String content;

    private String username;

    private String nickName;

    private String chatroomId;

    private LocalDateTime createdAt;

    public Chat toEntity(ChatRoom chatRoom, Member member) {
        return Chat.builder()
                .content(content)
                .chatRoom(chatRoom)
                .chatType(chatType)
                .member(member)
                .build();
    }
}