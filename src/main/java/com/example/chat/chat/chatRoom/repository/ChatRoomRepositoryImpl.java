package com.example.chat.chat.chatRoom.repository;

import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.chat.chatRoom.QChatRoom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.util.List;
import static com.example.chat.member.QMember.member;

@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatRoom> getChatRooms() {
        return queryFactory
                .selectFrom(QChatRoom.chatRoom)
                .join(QChatRoom.chatRoom.member, member).fetchJoin()
                .fetch();
    }

    @Override
    public ChatRoom findByChatRoomId(String chatRoomId) {
        return queryFactory
                .selectFrom(QChatRoom.chatRoom)
                .where(QChatRoom.chatRoom.chatRoomId.eq(chatRoomId))
                .fetchOne();
    }

    // 특정 채팅방의 정보 반환 ( member fetch join )
    @Override
    public ChatRoom findChatRoomWithMember(String chatRoomId) {
        return queryFactory
                .selectFrom(QChatRoom.chatRoom)
                .leftJoin(QChatRoom.chatRoom.member).fetchJoin()
                .where(QChatRoom.chatRoom.chatRoomId.eq(chatRoomId))
                .fetchOne();
    }
}