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
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

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
    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, HttpServletRequest request) {
        // 토큰 체크
        User user = checkJwtToken(request);

        // 게시글 DB 저장 유무 확인
        postRepository.findById(commentRequestDto.getPostId()).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );
        Comment comment = commentRepository.saveAndFlush(new Comment(commentRequestDto, user));
        return new CommentResponseDto(comment);
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto commentRequestDto, HttpServletRequest request) {
        // 토큰 체크
        User user = checkJwtToken(request);

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
    public Message deleteComment(Long id, HttpServletRequest request) {
        // 토큰 체크
        User user = checkJwtToken(request);

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


    public User checkJwtToken(HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 토큰이 있는 경우에만 게시글 접근 가능
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new CustomException(INVALIDATED_TOKEN);
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new CustomException(CANNOT_FOUND_USERNAME)
            );
            return user;
        }
        return null;
    }

}
