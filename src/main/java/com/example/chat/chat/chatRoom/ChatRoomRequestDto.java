package com.example.chat.chat.chatRoom;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ChatRoomRequestDto {

    private String chatRoomId;

    private String chatRoomName;

    private String creator;

    @Builder
    public ChatRoomRequestDto(String chatRoomId, String chatRoomName, String creator) {
        this.chatRoomId = chatRoomId;
        this.chatRoomName = chatRoomName;
        this.creator = creator;
    }
}
