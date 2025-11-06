package com.example.chat.chat.chatRoom.dto;

import com.example.chat.chat.chatRoom.ChatRoom;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomReqDto {

    private String chatRoomName;

    private String creator;

    public ChatRoom toChatRoom() {
        return ChatRoom.builder()
                .chatRoomName(this.chatRoomName)
                .build();
    }
}
