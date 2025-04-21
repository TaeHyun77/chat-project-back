package com.example.chat.chat.chatRoom;

import com.example.chat.member.MemberResDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatRoomMemberDto {

    private String chatRoomId;

    private String chatRoomName;

    private MemberResDto member;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @Builder
    public ChatRoomMemberDto(String chatRoomId, String chatRoomName, MemberResDto member, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.chatRoomId = chatRoomId;
        this.chatRoomName = chatRoomName;
        this.member = member;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static ChatRoomMemberDto from(ChatRoom chatRoom) {
        return ChatRoomMemberDto.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .chatRoomName(chatRoom.getChatRoomName())
                .createdAt(chatRoom.getCreatedAt())
                .modifiedAt(chatRoom.getModifiedAt())
                .build();
    }
}
