package com.example.chat.member.ui;

import com.example.chat.member.domain.Role;
import lombok.Getter;

@Getter
public class MemberReqDto {

    private String username;

    private String name;

    private String email;

    private Role role;
}
