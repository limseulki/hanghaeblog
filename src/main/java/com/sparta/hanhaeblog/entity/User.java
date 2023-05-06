package com.sparta.hanhaeblog.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable: null 허용 여부
    // unique: 중복 허용 여부 (false 일때 중복 허용)
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @JsonBackReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Post> postList = new ArrayList<>();

    @JsonBackReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Like> likeList = new ArrayList<>();

    // 일대다 관계 설정
    @JsonBackReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Comment> commentList = new ArrayList<>();

    @JsonBackReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Image> imageList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.REMOVE)
    private RefreshToken refreshToken;

    public User(String username, String password, UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public void update(RefreshToken updateToken) {
        this.refreshToken = updateToken;
    }
}