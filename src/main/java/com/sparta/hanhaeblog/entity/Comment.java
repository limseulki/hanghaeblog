package com.sparta.hanhaeblog.entity;

import com.sparta.hanhaeblog.dto.CommentRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int commentLike;

    // 다대일 관계 설정
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Comment(CommentRequestDto commentRequestDto, User user) {
        this.postId = commentRequestDto.getPostId();
        this.content = commentRequestDto.getContent();
        this.user= user;
        this.commentLike = 0;
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
