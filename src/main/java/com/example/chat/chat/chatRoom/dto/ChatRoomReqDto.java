package com.example.chat.chat.chatRoom.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomReqDto {

    private String chatRoomName;

    private String creator;

    @Builder
    public ChatRoomReqDto(String chatRoomName, String creator) {
        this.chatRoomName = chatRoomName;
        this.creator = creator;
    }
}
