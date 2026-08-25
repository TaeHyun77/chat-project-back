package com.example.chat.messaging.chatRoom.ui;

import com.example.chat.messaging.chatRoom.domain.ChatRoom;
import com.example.chat.member.ui.MemberResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatRoomResDto {

    private String chatRoomId;

    private String chatRoomName;

    private MemberResDto memberResDto;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public static ChatRoomResDto from(ChatRoom chatRoom) {
        return ChatRoomResDto.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .chatRoomName(chatRoom.getChatRoomName())
                .memberResDto(
                        MemberResDto.from(chatRoom.getMember())
                )
                .createdAt(chatRoom.getCreatedAt())
                .modifiedAt(chatRoom.getModifiedAt())
                .build();
    }
}
