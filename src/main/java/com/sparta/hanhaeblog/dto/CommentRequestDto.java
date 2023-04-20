package com.sparta.hanhaeblog.dto;

import lombok.Getter;

@Getter
public class CommentRequestDto {
    private Long id;
    private String content;
    private String username;
    private Long postId;
}
