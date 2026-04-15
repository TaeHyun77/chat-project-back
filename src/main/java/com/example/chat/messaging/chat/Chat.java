package com.example.chat.messaging.chat;

import com.example.chat.messaging.chatRoom.ChatRoom;
import com.example.chat.common.BaseTime;
import com.example.chat.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Chat extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatId")
    private Long id;

    // 채팅 내용
    private String content;

    // 채팅방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomId")
    private ChatRoom chatRoom;

    // 채팅을 작성한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    // 채팅의 타입
    @Enumerated(EnumType.STRING)
    private ChatType chatType;

    @Builder
    public Chat(String content, ChatRoom chatRoom, ChatType chatType, Member member) {
        this.content = content;
        this.chatRoom = chatRoom;
        this.chatType = chatType;
        this.member = member;
    }
}