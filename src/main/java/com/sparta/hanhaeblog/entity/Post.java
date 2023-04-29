package com.sparta.hanhaeblog.entity;

import com.sparta.hanhaeblog.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Post extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private int postLike;

    // 다대일 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 일대다 관계 설정
    @OneToMany
    private List<Comment> commentList = new ArrayList<>();

    public Post(PostRequestDto requestDto, String username, User user) {
        this.title = requestDto.getTitle();
        this.username = username;
        this.contents = requestDto.getContents();
        this.postLike = 0;
        this.user = user;
    }

    public void update(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
    }

    public void like() {
        this.postLike += 1;
    }

    public void unlike() {
        this.postLike -= 1;
    }
}
