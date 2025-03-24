package com.example.chat.chat.chatRoom;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ChatRoomResDto {

    private String chatRoomName;

    private String creator;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @Builder
    public ChatRoomResDto (String chatRoomName, String creator, LocalDateTime createdAt, LocalDateTime modifiedAt){
        this.chatRoomName = chatRoomName;
        this.creator = creator;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
