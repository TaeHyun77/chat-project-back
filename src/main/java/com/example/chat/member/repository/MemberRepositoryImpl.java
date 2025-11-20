package com.example.chat.member.repository;

import com.example.chat.member.Member;
import com.example.chat.member.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Member findByMemberWithChatRooms(Long id) {

        return queryFactory
                .selectFrom(QMember.member)
                .leftJoin(QMember.member.chatRooms).fetchJoin()
                .where(QMember.member.id.eq(id))
                .distinct()
                .fetchOne();
    }
}
