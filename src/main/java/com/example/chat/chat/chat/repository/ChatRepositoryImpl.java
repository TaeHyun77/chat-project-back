package com.example.chat.chat.chat.repository;

import com.example.chat.chat.chat.Chat;
import com.example.chat.chat.chat.QChat;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.chat.member.QMember.member;

@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Chat> getChats(String chatRoomId) {
        return queryFactory
                .selectFrom(QChat.chat)
                .join(QChat.chat.member, member).fetchJoin()
                .where(QChat.chat.chatRoom.chatRoomId.eq(chatRoomId))
                .orderBy(QChat.chat.createdAt.asc())
                .fetch();
    }
}
