package com.example.chat.messaging.chat.infrastructure;
import com.example.chat.messaging.chat.domain.ChatRepositoryCustom;

import com.example.chat.messaging.chat.domain.Chat;
import com.example.chat.messaging.chat.domain.QChat;
import com.example.chat.member.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Chat> getChatsByChatRoomId(String chatRoomId) {
        return queryFactory
                .selectFrom(QChat.chat)
                .join(QChat.chat.member, QMember.member).fetchJoin()
                .where(QChat.chat.chatRoom.chatRoomId.eq(chatRoomId))
                .orderBy(QChat.chat.createdAt.asc())
                .fetch();
    }
}
