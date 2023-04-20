package com.sparta.hanhaeblog.controller;

import com.sparta.hanhaeblog.dto.*;
import com.sparta.hanhaeblog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("")
    public CommentResponseDto createPost(@RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request) {
        return commentService.createComment(commentRequestDto, request);
    }

    @PutMapping("/{id}")
    public CommentResponseDto updatePost(@PathVariable Long id, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request) {
        return commentService.updateComment(id, commentRequestDto, request);
    }

    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id, HttpServletRequest request) {
        return commentService.deleteComment(id, request);
    }
}