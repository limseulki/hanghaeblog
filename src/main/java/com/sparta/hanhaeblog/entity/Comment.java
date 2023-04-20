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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    public Comment(CommentRequestDto commentRequestDto, User user) {
        this.postId = commentRequestDto.getPostId();
        this.content = commentRequestDto.getContent();
        this.user= user;
    }

    public void update(CommentRequestDto commentRequestDto, User user) {
        this.postId = commentRequestDto.getPostId();
        this.content = commentRequestDto.getContent();
        this.user = user;
    }
}
