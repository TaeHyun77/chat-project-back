package com.example.chat.messaging.chatRoom.ui;

import com.example.chat.messaging.chatRoom.domain.ChatRoom;
import com.example.chat.member.domain.Member;
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
