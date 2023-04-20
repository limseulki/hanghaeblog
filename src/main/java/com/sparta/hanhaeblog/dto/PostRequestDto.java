package com.sparta.hanhaeblog.dto;

import lombok.Getter;

@Getter
public class PostRequestDto {
    private Long id;
    private String title;
    private String contents;
    private String username;
}
