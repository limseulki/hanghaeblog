package com.sparta.hanhaeblog.dto;

import com.sparta.hanhaeblog.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String contents;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String username;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.contents = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
        this.username = comment.getUser().getUsername();
    }
}
