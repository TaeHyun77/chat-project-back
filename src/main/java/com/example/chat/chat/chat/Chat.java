package com.example.chat.chat.chat;

import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.config.BaseTime;
import com.example.chat.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Chat extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatId")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomId")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

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