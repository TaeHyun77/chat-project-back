package com.example.chat.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberDto {

    private String username;

    private String name;

    private String email;

    private Role role;

    @Builder
    public MemberDto(String username, String name, String email, Role role) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Member toMemberEntity() {
        return Member.builder()
                .username(username)
                .name(name)
                .email(email)
                .role(role)
                .build();
    }
}
