package com.example.chat.chat.chatRoom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.chat.chat.chat.QChat.chat;
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

    @Override
    public ChatRoom findChatRoomWithChatsAndMember(String chatRoomId) {
        return queryFactory
                .selectFrom(QChatRoom.chatRoom)
                .leftJoin(QChatRoom.chatRoom.member).fetchJoin()
                .leftJoin(QChatRoom.chatRoom.chats, chat).fetchJoin()
                .leftJoin(chat.member, member).fetchJoin()
                .where(QChatRoom.chatRoom.chatRoomId.eq(chatRoomId))
                .distinct()
                .fetchOne();
    }
}