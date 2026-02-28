package com.example.chat.messaging.chatRoom.dto;

import com.example.chat.messaging.chatRoom.ChatRoom;
import com.example.chat.messaging.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomReqDto {

    private String chatRoomName;

    private String creator;

    public ChatRoom toEntity(Member member) {
        return ChatRoom.builder()
                .chatRoomName(chatRoomName)
                .member(member)
                .build();
    }
}
