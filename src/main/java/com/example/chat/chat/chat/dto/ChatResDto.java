package com.example.chat.chat.chat.dto;

import com.example.chat.chat.chat.Chat;
import com.example.chat.chat.chat.ChatType;
import com.example.chat.member.dto.MemberResDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ChatResDto {
    private ChatType chatType;
    private String content;
    private MemberResDto member;
    private LocalDateTime createdAt;

    @Builder
    public ChatResDto(ChatType chatType, String content, MemberResDto member, LocalDateTime createdAt) {
        this.chatType = chatType;
        this.content = content;
        this.member = member;
        this.createdAt = createdAt;
    }

    public static ChatResDto fromChatEntity(Chat chat) {
        return ChatResDto.builder()
                .chatType(chat.getChatType())
                .content(chat.getContent())
                .member(MemberResDto.fromMemberEntity(chat.getMember()))
                .createdAt(chat.getCreatedAt())
                .build();
    }
}

