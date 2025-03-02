package com.example.chat.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class ChatRoom {

    private String chatRoomId;

    private String name;

    @Builder
    public ChatRoom(String name) {
        this.chatRoomId = UUID.randomUUID().toString();
        this.name = name;
    }
}
