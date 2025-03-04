package com.example.chat.chat.chatMessage;

import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.config.BaseTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private ChatRoom chatRoom;

    @Enumerated(EnumType.STRING)
    private ChatType chatType;

    private String username;

    @Builder
    public Chat(String content, ChatRoom chatRoom, ChatType chatType, String username) {
        this.content = content;
        this.chatRoom = chatRoom;
        this.chatType = chatType;
        this.username = username;
    }
}