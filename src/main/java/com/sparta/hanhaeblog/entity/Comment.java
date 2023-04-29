package com.sparta.hanhaeblog.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.hanhaeblog.dto.CommentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postsId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int commentLike;

    // 다대일 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    // 일대다 관계 설정
    @JsonBackReference
    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private List<Like> likeList = new ArrayList<>();

    public Comment(CommentRequestDto commentRequestDto, User user, Post post) {
        this.postsId = commentRequestDto.getPostId();
        this.content = commentRequestDto.getContent();
        this.user= user;
        this.commentLike = 0;
        this.post = post;
    }

    public void update(CommentRequestDto commentRequestDto, User user) {
        this.content = commentRequestDto.getContent();
        this.user = user;
    }

    public void like() {
        this.commentLike += 1;
    }

    public void unlike() {
        this.commentLike -= 1;
    }
}
