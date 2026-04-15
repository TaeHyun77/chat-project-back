package com.example.chat.messaging.chat.dto;

import com.example.chat.messaging.chat.Chat;
import com.example.chat.messaging.chat.ChatType;
import com.example.chat.member.dto.MemberResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class ChatResDto {
    private ChatType chatType;
    private String content;
    private MemberResDto memberResDto;
    private LocalDateTime createdAt;

    public static ChatResDto from(Chat chat) {
        return ChatResDto.builder()
                .chatType(chat.getChatType())
                .content(chat.getContent())
                .memberResDto(
                        MemberResDto.from(chat.getMember())
                )
                .createdAt(chat.getCreatedAt())
                .build();
    }
}

