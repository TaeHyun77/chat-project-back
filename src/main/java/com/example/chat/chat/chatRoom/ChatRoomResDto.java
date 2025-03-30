package com.example.chat.chat.chatRoom;

import com.example.chat.chat.chat.Chat;
import com.example.chat.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class ChatRoomResDto {

    private Long id;

    private String chatRoomName;

    private Member member;

    private List<Chat> chats;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @Builder
    public ChatRoomResDto (Long id, String chatRoomName,Member member,List<Chat> chats, LocalDateTime createdAt, LocalDateTime modifiedAt){
        this.id = id;
        this.chatRoomName = chatRoomName;
        this.member = member;
        this.chats = chats;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
