package com.sparta.hanhaeblog.controller;

import com.sparta.hanhaeblog.Message.Message;
import com.sparta.hanhaeblog.security.UserDetailsImpl;
import com.sparta.hanhaeblog.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class LikeController {

    private final LikeService likeService;

    // Post 좋아요
    @PostMapping("/post/{postId}")
    public Message postLike(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.postLike(postId, userDetails.getUser());
    }

    // Post 좋아요 취소
//    @DeleteMapping("/post/{postId}")
//    public Message postUnLike(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return likeService.postUnLike(postId, userDetails.getUser());
//    }

    // Comment 좋아요
    @PostMapping("/comment/{commentId}")
    public Message commentLike(@PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.commentLike(commentId, userDetails.getUser());
    }

    // Comment 좋아요 취소
//    @DeleteMapping("/comment/{commentId}")
//    public Message commentLike(@PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return likeService.commentUnLike(commentId, userDetails.getUser());
//    }
}
