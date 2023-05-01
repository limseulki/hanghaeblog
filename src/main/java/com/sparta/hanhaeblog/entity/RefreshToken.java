package com.sparta.hanhaeblog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;


@Getter
@Entity
@NoArgsConstructor(force = true)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String refreshToken;

    @NotNull
    private String username;

    @OneToOne
    private User user;

    public RefreshToken(String refreshToken, String username, User user) {
        this.refreshToken = refreshToken;
        this.username = username;
        this.user = user;
    }

    public RefreshToken updateToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
