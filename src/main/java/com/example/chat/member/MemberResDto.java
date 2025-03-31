package com.example.chat.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberResDto {

    private Long id;

    private String username;

    private String name;

    private String email;

    private String nickName;

    private Role role;

    @Builder
    public MemberResDto(Long id, String username, String name, String email, String nickName, Role role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.nickName = nickName;
        this.role = role;
    }

    public static MemberResDto fromMemberEntity(Member member) {
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
