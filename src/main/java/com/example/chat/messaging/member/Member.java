package com.example.chat.messaging.member;

import com.example.chat.messaging.chat.Chat;
import com.example.chat.messaging.chatRoom.ChatRoom;
import com.example.chat.common.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "member")
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberId")
    private Long id;

    // 로그인 아이디
    private String username;

    // 사용자 이름
    private String name;

    // 사용자 별명
    private String nickName;

    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 해당 member가 만든 채팅 방 리스트
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRooms = new ArrayList<>();

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

    public void changeNickName(String nickName) {
        this.nickName = nickName;
    }
}
