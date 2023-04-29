package com.sparta.hanhaeblog.service;

import com.sparta.hanhaeblog.Exception.CustomException;
import com.sparta.hanhaeblog.Message.Message;
import com.sparta.hanhaeblog.dto.CommentRequestDto;
import com.sparta.hanhaeblog.dto.CommentResponseDto;
import com.sparta.hanhaeblog.entity.Comment;
import com.sparta.hanhaeblog.entity.Post;
import com.sparta.hanhaeblog.entity.User;
import com.sparta.hanhaeblog.entity.UserRoleEnum;
import com.sparta.hanhaeblog.repository.CommentRepository;
import com.sparta.hanhaeblog.repository.LikeRepository;
import com.sparta.hanhaeblog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sparta.hanhaeblog.Exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    Comment comment;
    Post post;

    // 댓글 작성
    @Transactional
    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, User user) {
        post = existPost(commentRequestDto);
        comment = commentRepository.saveAndFlush(new Comment(commentRequestDto, user, post));
        return new CommentResponseDto(comment);
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto commentRequestDto, User user) {
        comment = checkRole(id, user);
        comment.update(commentRequestDto, user);
        return new CommentResponseDto(comment);
    }

    // 댓글 삭제
    @Transactional
    public Message deleteComment(Long id, User user) {
        comment = checkRole(id, user);
        commentRepository.deleteById(id);
        return new Message("댓글 삭제 성공", 200);
    }

    // 작성자 일치 여부 확인
    private Comment matchAuthor(Long id, User user) {
        comment = commentRepository.findByIdAndUser_username(id, user.getUsername()).orElseThrow(
                () -> new CustomException(AUTHOR_NOT_SAME_DEL)
        );
        return comment;
    }

    // 댓글이 DB 유무 확인
    private Comment existComment(Long id) {
        comment = commentRepository.findById(id).orElseThrow(
                () -> new CustomException(COMMENT_NOT_FOUND)
        );
        return comment;
    }

    // 게시글 DB 저장 유무 확인
    private Post existPost(CommentRequestDto commentRequestDto) {
        post = postRepository.findById(commentRequestDto.getPostId()).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );
        return post;
    }

    // 관리자 여부 확인
    private Comment checkRole(Long id, User user) {
        UserRoleEnum userRoleEnum = user.getRole();
        if(userRoleEnum == UserRoleEnum.ADMIN) {
            comment = existComment(id);
        } else {
            comment = matchAuthor(id, user);
        }
        return comment;
    }
}
