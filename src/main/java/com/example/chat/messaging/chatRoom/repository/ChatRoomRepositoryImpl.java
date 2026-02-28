package com.example.chat.messaging.chatRoom.repository;

import com.example.chat.messaging.chatRoom.ChatRoom;
import com.example.chat.messaging.chatRoom.QChatRoom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.util.List;
import static com.example.chat.member.QMember.member;

@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 채팅방 목록 조회
    @Override
    public List<ChatRoom> getChatRooms() {
        return queryFactory
                .selectFrom(QChatRoom.chatRoom)
                .join(QChatRoom.chatRoom.member, member).fetchJoin()
                .fetch();
    }

    // 특정 사용자의 채팅방 목록 조회
    @Override
    public List<ChatRoom> findChatroomsByMemberId(Long memberId) {
        return queryFactory
                .selectFrom(QChatRoom.chatRoom)
                .where(QChatRoom.chatRoom.member.id.eq(memberId))
                .fetch();
    }
}