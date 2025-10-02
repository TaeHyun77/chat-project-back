package com.example.chat.member;

import com.example.chat.member.dto.MemberReqDto;
import com.example.chat.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 등록 테스트")
    public void save_member_test() {

        MemberReqDto dto = MemberReqDto.builder()
                .username("asdfg")
                .name("박태현")
                .email("qwer")
                .role(Role.MEMBER)
                .build();

        memberRepository.save(dto.toMemberEntity());

        assertEquals(dto.getUsername(), "asdfg");
        assertEquals(dto.getName(), "박태현");
        assertEquals(dto.getEmail(), "qwer");
        assertEquals(dto.getRole(), Role.MEMBER);
    }
}