package com.sparta.hanhaeblog.entity;

import com.sparta.hanhaeblog.dto.PostRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    // 일대다 관계 설정
    @OneToMany
    private List<Comment> commentList = new ArrayList<>();

    public Post(PostRequestDto requestDto, String username) {
        this.title = requestDto.getTitle();
        this.username = username;
        this.contents = requestDto.getContents();
        this.postLike = 0;
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
