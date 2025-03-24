package com.example.chat.chat.chatRoom;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomRequestDto {

    private String chatRoomName;

    private String creator;

    @Builder
    public ChatRoomRequestDto(String chatRoomName, String creator) {
        this.chatRoomName = chatRoomName;
        this.creator = creator;
    }
}
