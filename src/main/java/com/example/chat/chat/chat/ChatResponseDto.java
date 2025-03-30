package com.example.chat.chat.chat;

import com.example.chat.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ChatResponseDto {
    private ChatType chatType;
    private String content;
    private Member member;
    private LocalDateTime createdAt;

    @Builder
    public ChatResponseDto(ChatType chatType, String content,Member member, LocalDateTime createdAt) {
        this.chatType = chatType;
        this.content = content;
        this.member = member;
        this.createdAt = createdAt;
    }
}

