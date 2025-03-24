package com.example.chat.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberResponseDto {

    private Long id;

    private String username;

    private String name;

    private String email;

    private String nickName;

    private Role role;

    @Builder
    public MemberResponseDto(Long id, String username, String name, String email, String nickName, Role role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.nickName = nickName;
        this.role = role;
    }
}
