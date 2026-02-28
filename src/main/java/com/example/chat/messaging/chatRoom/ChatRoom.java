package com.example.chat.messaging.chatRoom;

import com.example.chat.messaging.chat.Chat;
import com.example.chat.common.BaseTime;
import com.example.chat.messaging.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Table(name = "chatroom")
@Entity
public class ChatRoom extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roomId")
    private Long id;

    // 채팅방 ID
    @Column(unique = true, nullable = false)
    private String chatRoomId;

    // 채팅방 이름
    private String chatRoomName;

    // 채팅방을 생성한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    // 채팅방의 채팅들
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chats = new ArrayList<>();

    @Builder
    public ChatRoom(String chatRoomName, Member member) {
        this.chatRoomId = UUID.randomUUID().toString();
        this.chatRoomName = chatRoomName;
        this.member = member;
    }
}


