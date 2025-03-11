package com.example.chat.chat.chatRoom;

import com.example.chat.chat.chatMessage.Chat;
import com.example.chat.config.BaseTime;
import com.example.chat.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "chatroom")
@Entity
public class ChatRoom extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roomId")
    private Long id;

    @Column(unique = true, nullable = false)
    private String chatRoomId;

    private String chatRoomName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    @JsonIgnore
    private Member member;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chats = new ArrayList<>();

    @Builder
    public ChatRoom(String chatRoomId, String chatRoomName) {
        this.chatRoomId = UUID.randomUUID().toString();
        this.chatRoomName = chatRoomName;
    }
}
