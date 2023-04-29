package com.sparta.hanhaeblog.service;

import com.sparta.hanhaeblog.Exception.CustomException;
import com.sparta.hanhaeblog.Message.Message;
import com.sparta.hanhaeblog.entity.Comment;
import com.sparta.hanhaeblog.entity.Like;
import com.sparta.hanhaeblog.entity.Post;
import com.sparta.hanhaeblog.entity.User;
import com.sparta.hanhaeblog.repository.CommentRepository;
import com.sparta.hanhaeblog.repository.LikeRepository;
import com.sparta.hanhaeblog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sparta.hanhaeblog.Exception.ErrorCode.COMMENT_NOT_FOUND;
import static com.sparta.hanhaeblog.Exception.ErrorCode.POST_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    // Post 좋아요
    @Transactional
    public Message postLike(Long postId, User user) {

        // 1. 게시글 조회
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );

        // 2. LikeRepository 에서 postId, userId 로 조회
        Like like = likeRepository.findByPostIdAndUser_Id(postId, user.getId());

        // 좋아요 없으면 DB에 추가
        if (like == null) {
            likeRepository.save(new Like(user, post, null));
            post.like();
            return new Message("게시글 좋아요 성공", 200);
        } else { // 좋아요 있으면 DB에서 제거
            likeRepository.deleteById(like.getId());
            post.unlike();
            return new Message("게시글 좋아요 취소 성공", 200);
        }
    }

    // Comment 좋아요
    @Transactional
    public Message commentLike(Long commentId, User user) {

        // 1. 댓글 조회
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(COMMENT_NOT_FOUND)
        );

        // 2. LikeRepository 에서 commentId, userId 로 조회
        Like like = likeRepository.findByCommentIdAndUser_Id(commentId, user.getId());

        // 좋아요 없으면 DB에 추가
        if (like == null) {
            likeRepository.save(new Like(user, null, comment));
            comment.like();
            return new Message("댓글 좋아요 성공", 200);
        } else { // 좋아요 있으면 DB에서 제거
            likeRepository.deleteById(like.getId());
            comment.unlike();
            return new Message("댓글 좋아요 취소 성공", 200);
        }
    }
}
