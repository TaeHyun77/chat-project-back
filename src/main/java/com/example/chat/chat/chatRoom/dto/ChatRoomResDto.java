package com.example.chat.chat.chatRoom.dto;

import com.example.chat.member.dto.MemberResDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ChatRoomResDto {

    private String chatRoomId;

    private String chatRoomName;

    private MemberResDto member;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @Builder
    public ChatRoomResDto (String chatRoomId, String chatRoomName, MemberResDto member, LocalDateTime createdAt, LocalDateTime modifiedAt){
        this.chatRoomId = chatRoomId;
        this.chatRoomName = chatRoomName;
        this.member = member;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
