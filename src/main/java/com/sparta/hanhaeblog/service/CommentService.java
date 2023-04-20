package com.sparta.hanhaeblog.service;

import com.sparta.hanhaeblog.dto.CommentRequestDto;
import com.sparta.hanhaeblog.dto.CommentResponseDto;
import com.sparta.hanhaeblog.dto.PostResponseDto;
import com.sparta.hanhaeblog.entity.Comment;
import com.sparta.hanhaeblog.entity.Post;
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

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, HttpServletRequest request) {
        // 토큰 체크
        User user = checkJwtToken(request);

        // 댓글 DB 저장 유무 확인
        Post post = postRepository.findById(commentRequestDto.getPostId()).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다.")
        );

        Comment comment = new Comment(commentRequestDto);
        comment.setUsername(user.getUsername());

        commentRepository.saveAndFlush(comment);
        return new CommentResponseDto(comment);
    }

    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto commentRequestDto, HttpServletRequest request) {
        // 토큰 체크
        User user = checkJwtToken(request);

        // 댓글 DB 저장 유무 확인
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 댓글이 존재하지 않습니다.")
        );

        UserRoleEnum userRoleEnum = user.getRole();
        System.out.println("role = " + userRoleEnum);

        // 권한 확인 후, 관리자가 아니면 작성자인지 확인
        if(userRoleEnum == UserRoleEnum.ADMIN) {
            comment.update(commentRequestDto);
            return new CommentResponseDto(comment);
        } else {
            if(comment.getUsername() != user.getUsername()) {
                throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
            }

            comment.update(commentRequestDto);
            return new CommentResponseDto(comment);
        }
    }

    @Transactional
    public String deleteComment(Long id, HttpServletRequest request) {
        // 토큰 체크
        User user = checkJwtToken(request);

        // 댓글 DB 저장 유무 확인
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 댓글이 존재하지 않습니다.")
        );

        UserRoleEnum userRoleEnum = user.getRole();
        System.out.println("role = " + userRoleEnum);

        // 권한 확인 후, 관리자가 아니면 작성자인지 확인
        if(userRoleEnum == UserRoleEnum.ADMIN) {
            commentRepository.delete(comment);
            return "댓글 삭제 성공";
        } else {
            if(comment.getUsername() != user.getUsername()) {
                throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
            }

            commentRepository.delete(comment);
            return "댓글 삭제 성공";
        }
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
                throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );
            return user;

        }
        return null;
    }

}
