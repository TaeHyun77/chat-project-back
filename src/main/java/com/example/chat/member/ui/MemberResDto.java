package com.example.chat.member.ui;

import com.example.chat.messaging.chatRoom.ui.ChatRoomResDto;
import com.example.chat.member.domain.Member;
import com.example.chat.member.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberResDto {

    private Long id;

    private String username;

    private String name;

    private String email;

    private String nickName;

    private Role role;

    private List<ChatRoomResDto> chatRooms;

    public static MemberResDto from(Member member) {
        return MemberResDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .name(member.getName())
                .email(member.getEmail())
                .nickName(member.getNickName())
                .role(member.getRole())
                .build();
    }
}
