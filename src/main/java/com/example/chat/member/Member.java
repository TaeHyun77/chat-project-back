package com.example.chat.member;

import com.example.chat.chat.chatRoom.ChatRoom;
import com.example.chat.chat.chatRoom.ChatRoomRequestDto;
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
@Table(name = "member")
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String name;

    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomId")
    @JsonIgnore
    private ChatRoom chatRoom;

    @Builder
    public Member(String username, String name, String email, Role role) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public boolean isNew() {
        return this.id == null;
    }
}
