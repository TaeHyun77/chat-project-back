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

    private LocalDateTime createdAt;

    private String roomId;

    public Chat toChat(ChatRoom chatRoom, Member member) {
        return Chat.builder()
                .content(this.getContent())
                .chatRoom(chatRoom)
                .chatType(this.getChatType())
                .member(member)
                .build();
    }
}