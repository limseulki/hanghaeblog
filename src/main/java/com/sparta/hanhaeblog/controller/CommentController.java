package com.sparta.hanhaeblog.controller;

import com.sparta.hanhaeblog.Message.Message;
import com.sparta.hanhaeblog.dto.*;
import com.sparta.hanhaeblog.entity.Comment;
import com.sparta.hanhaeblog.security.UserDetailsImpl;
import com.sparta.hanhaeblog.service.CommentService;
import com.sparta.hanhaeblog.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;
    private final SearchService searchService;

    // 댓글 작성
    @PostMapping("")
    public CommentResponseDto createComment(@RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.createComment(commentRequestDto, userDetails.getUser());
    }

    // 댓글 수정
    @PutMapping("/{id}")
    public CommentResponseDto updateComment(@PathVariable Long id, @RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.updateComment(id, commentRequestDto, userDetails.getUser());
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public Message deleteComment(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.deleteComment(id, userDetails.getUser());
    }

    // 댓글 검색
    @GetMapping("/search")
    public List<CommentResponseDto> searchPost(@RequestParam String keyword) {
        return searchService.searchComment(keyword);
    }
}
