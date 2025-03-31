package com.example.chat.chat.chatRoom;

import com.example.chat.chat.chat.ChatResDto;
import com.example.chat.member.MemberResDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class ChatRoomResDto {

    private String chatRoomId;

    private String chatRoomName;

    private MemberResDto member;

    private List<ChatResDto> chats;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @Builder
    public ChatRoomResDto (String chatRoomId, String chatRoomName, MemberResDto member, List<ChatResDto> chats, LocalDateTime createdAt, LocalDateTime modifiedAt){
        this.chatRoomId = chatRoomId;
        this.chatRoomName = chatRoomName;
        this.member = member;
        this.chats = chats;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
