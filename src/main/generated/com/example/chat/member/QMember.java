package com.example.chat.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 615817225L;

    public static final QMember member = new QMember("member1");

    public final com.example.chat.config.QBaseTime _super = new com.example.chat.config.QBaseTime(this);

    public final ListPath<com.example.chat.chat.chatRoom.ChatRoom, com.example.chat.chat.chatRoom.QChatRoom> chatRooms = this.<com.example.chat.chat.chatRoom.ChatRoom, com.example.chat.chat.chatRoom.QChatRoom>createList("chatRooms", com.example.chat.chat.chatRoom.ChatRoom.class, com.example.chat.chat.chatRoom.QChatRoom.class, PathInits.DIRECT2);

    public final ListPath<com.example.chat.chat.chat.Chat, com.example.chat.chat.chat.QChat> chats = this.<com.example.chat.chat.chat.Chat, com.example.chat.chat.chat.QChat>createList("chats", com.example.chat.chat.chat.Chat.class, com.example.chat.chat.chat.QChat.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath name = createString("name");

    public final StringPath nickName = createString("nickName");

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final StringPath username = createString("username");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

