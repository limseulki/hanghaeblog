package com.sparta.hanhaeblog.entity;

import com.sparta.hanhaeblog.dto.CommentRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Long postId;

    public Comment(CommentRequestDto commentRequestDto) {
        this.content = commentRequestDto.getContent();
        this.username = commentRequestDto.getUsername();
        this.postId = commentRequestDto.getPostId();
    }

    public void update(CommentRequestDto commentRequestDto) {
        this.content = commentRequestDto.getContent();
    }
}
