package com.example.chat.chat.chatRoom.dto;

import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.member.Member;
import lombok.Builder;
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
