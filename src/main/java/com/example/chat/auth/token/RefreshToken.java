package com.example.chat.auth.token;

import com.example.chat.common.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class RefreshToken extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String refresh;

    private LocalDateTime expiration;

    @Version
    private Integer version;

    @Builder
    public RefreshToken(String username, String refresh, LocalDateTime expiration) {
        this.username = username;
        this.refresh = refresh;
        this.expiration = expiration;
    }
}
