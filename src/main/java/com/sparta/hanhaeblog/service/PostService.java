package com.sparta.hanhaeblog.service;

import com.sparta.hanhaeblog.dto.ModifiedResponseDto;
import com.sparta.hanhaeblog.dto.PostRequestDto;
import com.sparta.hanhaeblog.dto.PostResponseDto;
import com.sparta.hanhaeblog.entity.Post;
import com.sparta.hanhaeblog.entity.User;
import com.sparta.hanhaeblog.jwt.JwtUtil;
import com.sparta.hanhaeblog.repository.PostRepository;
import com.sparta.hanhaeblog.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;


    // Post 작성
    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto, HttpServletRequest request) {
        // 토큰 체크
        User user = checkJwtToken(request);

        Post post = new Post(requestDto);
        post.setUsername(user.getUsername());
        postRepository.saveAndFlush(post);
        return new PostResponseDto(post);

    }


    // 전체 Post 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(PostResponseDto::new).collect(Collectors.toList());
    }

    // 선택한 Post 조회
    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 글이 존재하지 않습니다.")
        );
        return new PostResponseDto(post);
    }

    // 선택한 Post 수정
    @Transactional
    public ModifiedResponseDto updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
        // 토큰 체크
        User user = checkJwtToken(request);

        Post post = postRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 글이 존재하지 않습니다.")
        );

        if(post.getUsername() != user.getUsername()) {
            throw new IllegalArgumentException("다른 사람의 게시글은 수정 할 수 없습니다.");
        }

        post.update(requestDto);
        return new ModifiedResponseDto(post);
    }

    // 선택한 Post 삭제
    @Transactional
    public String deletePost(Long id, HttpServletRequest request) {
        // 토큰 체크
        User user = checkJwtToken(request);

        Post post = postRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 글이 존재하지 않습니다.")
        );

        if(post.getUsername() != user.getUsername()) {
            throw new IllegalArgumentException("다른 사람의 게시글은 삭제 할 수 없습니다.");
        }

        postRepository.delete(post);
        return "게시글을 삭제했습니다.";

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
                throw new IllegalArgumentException("Token Error");
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