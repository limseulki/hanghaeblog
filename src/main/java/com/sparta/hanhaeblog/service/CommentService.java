package com.sparta.hanhaeblog.service;

import com.sparta.hanhaeblog.Exception.CustomException;
import com.sparta.hanhaeblog.Message.Message;
import com.sparta.hanhaeblog.dto.CommentRequestDto;
import com.sparta.hanhaeblog.dto.CommentResponseDto;
import com.sparta.hanhaeblog.entity.Comment;
import com.sparta.hanhaeblog.entity.User;
import com.sparta.hanhaeblog.entity.UserRoleEnum;
import com.sparta.hanhaeblog.jwt.JwtUtil;
import com.sparta.hanhaeblog.repository.CommentRepository;
import com.sparta.hanhaeblog.repository.PostRepository;
import com.sparta.hanhaeblog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sparta.hanhaeblog.Exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final JwtUtil jwtUtil;

    // 댓글 작성
    @Transactional
    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, User user) {

        // 게시글 DB 저장 유무 확인
        postRepository.findById(commentRequestDto.getPostId()).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );
        Comment comment = commentRepository.saveAndFlush(new Comment(commentRequestDto, user));
        return new CommentResponseDto(comment);
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto commentRequestDto, User user) {

        UserRoleEnum userRoleEnum = user.getRole();

        Comment comment;
        // 권한 확인 후, 관리자가 아니면 작성자인지 확인
        if(userRoleEnum == UserRoleEnum.ADMIN) {
             comment = commentRepository.findById(id).orElseThrow(
                    () -> new CustomException(COMMENT_NOT_FOUND)
            );
        } else {
            comment = commentRepository.findByIdAndUser_username(id, user.getUsername()).orElseThrow(
                    () -> new CustomException(AUTHOR_NOT_SAME_MOD)
            );
        }
        comment.update(commentRequestDto, user);
        return new CommentResponseDto(comment);
    }

    // 댓글 삭제
    @Transactional
    public Message deleteComment(Long id, User user) {

        UserRoleEnum userRoleEnum = user.getRole();

        Comment comment;
        // 권한 확인 후, 관리자가 아니면 작성자인지 확인
        if(userRoleEnum == UserRoleEnum.ADMIN) {
            comment = commentRepository.findById(id).orElseThrow(
                    () -> new CustomException(COMMENT_NOT_FOUND)
            );
        } else {
            comment = commentRepository.findByIdAndUser_username(id, user.getUsername()).orElseThrow(
                    () -> new CustomException(AUTHOR_NOT_SAME_DEL)
            );
        }
        commentRepository.deleteById(id);
        return new Message("댓글 삭제 성공", 200);
    }
}
