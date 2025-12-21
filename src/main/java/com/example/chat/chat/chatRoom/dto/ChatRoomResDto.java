package com.example.chat.chat.chatRoom.dto;

import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.member.dto.MemberResDto;
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


    // Query Dsl Constructor 사용을 위한 생성자
    public ChatRoomResDto(String chatRoomId, String chatRoomName, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.chatRoomId = chatRoomId;
        this.chatRoomName = chatRoomName;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
