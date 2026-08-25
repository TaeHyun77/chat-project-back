package com.example.chat.messaging.chat.ui;

import com.example.chat.messaging.chat.domain.Chat;
import com.example.chat.messaging.chat.domain.ChatType;
import com.example.chat.member.ui.MemberResDto;
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

