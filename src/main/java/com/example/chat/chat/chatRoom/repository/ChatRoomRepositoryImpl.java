package com.example.chat.chat.chatRoom.repository;

import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.chat.chatRoom.QChatRoom;
import com.example.chat.chat.chatRoom.dto.ChatRoomResDto;
import com.example.chat.member.QMember;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

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
    public List<ChatRoomResDto> findChatroomsByMemberId(Long memberId) {
        return queryFactory
                .select(Projections.constructor(
                        ChatRoomResDto.class,
                        QChatRoom.chatRoom.chatRoomId,
                        QChatRoom.chatRoom.chatRoomName,
                        QChatRoom.chatRoom.createdAt,
                        QChatRoom.chatRoom.modifiedAt
                ))
                .from(QChatRoom.chatRoom)
                .join(QChatRoom.chatRoom.member, QMember.member)
                .where(QMember.member.id.eq(memberId))
                .fetch();
    }
}