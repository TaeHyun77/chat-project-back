package com.example.chat.member.repository;

import com.example.chat.member.Member;

public interface MemberRepositoryCustom {

    Member findByMemberWithChatRooms(Long id);

}
