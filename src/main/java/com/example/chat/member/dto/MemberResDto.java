package com.example.chat.member.dto;

import com.example.chat.chat.chatRoom.dto.ChatRoomResDto;
import com.example.chat.member.Member;
import com.example.chat.member.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Builder
    public MemberResDto(Long id, String username, String name, String email, String nickName, Role role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.nickName = nickName;
        this.role = role;
    }

    public MemberResDto(String nickName) {
        this.nickName = nickName;
    }

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
