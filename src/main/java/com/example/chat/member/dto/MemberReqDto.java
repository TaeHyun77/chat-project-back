package com.example.chat.member.dto;

import com.example.chat.member.Member;
import com.example.chat.member.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberReqDto {

    private String username;

    private String name;

    private String email;

    private Role role;
}
