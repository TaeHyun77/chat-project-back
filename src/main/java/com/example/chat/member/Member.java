package com.example.chat.member;

import com.example.chat.chat.chat.Chat;
import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.config.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.repository.cdi.Eager;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "member")
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberId")
    private Long id;

    private String username;

    private String name;

    private String email;

    private String nickName;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 해당 member가 만든 채팅 방 리스트
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Chat> chats;

    @Builder
    public Member(String username, String name, String email, String nickName, Role role) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.nickName = nickName;
        this.role = role;
    }

    public boolean isNew() {
        return this.id == null;
    }
}
