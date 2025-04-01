package com.example.chat.chat.chatRoom;

import com.example.chat.chat.chat.QChat;
import com.example.chat.member.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatRoom> getChatRooms() {
        return queryFactory
                .selectFrom(QChatRoom.chatRoom)
                .join(QChatRoom.chatRoom.member, QMember.member).fetchJoin()
                .fetch();
    }

    @Override
    public ChatRoom findChatRoomWithChatsAndMember(String chatRoomId) {
        return queryFactory
                .selectFrom(QChatRoom.chatRoom)
                .leftJoin(QChatRoom.chatRoom.member).fetchJoin()
                .leftJoin(QChatRoom.chatRoom.chats, QChat.chat).fetchJoin()
                .leftJoin(QChat.chat.member, QMember.member).fetchJoin()
                .where(QChatRoom.chatRoom.chatRoomId.eq(chatRoomId))
                .distinct()
                .fetchOne();
    }

}