package com.sparta.hanhaeblog.service;

import com.sparta.hanhaeblog.dto.CommentResponseDto;
import com.sparta.hanhaeblog.dto.ModifiedResponseDto;
import com.sparta.hanhaeblog.dto.PostRequestDto;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;


    // Post 작성
    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto, HttpServletRequest request) {
        // 토큰 체크
        User user = checkJwtToken(request);

        List<CommentResponseDto> commentList = new ArrayList<>();

        Post post = postRepository.saveAndFlush(new Post(requestDto, user.getUsername()));
        return new PostResponseDto(post, commentList);

    }

    // 전체 Post 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostResponseDto> postList = new ArrayList<>();
        for(Post post : posts) {
            postList.add(new PostResponseDto(post, getCommentList(post.getId())));
        }
        return postList;
    }

    // 선택한 Post 조회
    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 글이 존재하지 않습니다.")
        );
        return new PostResponseDto(post, getCommentList(id));
    }

    // 선택한 Post 수정
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
        // 토큰 체크
        User user = checkJwtToken(request);

        Post post = postRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 글이 존재하지 않습니다.")
        );

        UserRoleEnum userRoleEnum = user.getRole();

        if(userRoleEnum == UserRoleEnum.ADMIN) {
            post.update(requestDto);
            return new PostResponseDto(post, getCommentList(id));
        } else {
            if(post.getUsername() != user.getUsername()) {
                throw new IllegalArgumentException("다른 사람의 게시글은 수정 할 수 없습니다.");
            }

            post.update(requestDto);
            return new PostResponseDto(post, getCommentList(id));
        }
    }

    // 선택한 Post 삭제
    @Transactional
    public String deletePost(Long id, HttpServletRequest request) {
        // 토큰 체크
        User user = checkJwtToken(request);

        Post post = postRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 글이 존재하지 않습니다.")
        );

        UserRoleEnum userRoleEnum = user.getRole();

        if(userRoleEnum == UserRoleEnum.ADMIN) {
            postRepository.delete(post);
            return "게시글 삭제 성공";
        } else {
            if(post.getUsername() != user.getUsername()) {
                throw new IllegalArgumentException("다른 사람의 게시글은 삭제 할 수 없습니다.");
            }

            postRepository.delete(post);
            return "게시글 삭제 성공";
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

    private List<CommentResponseDto> getCommentList(Long id) {
        List<Comment> commentList = commentRepository.findAllByIdOrderByCreatedAtDesc(id);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        for(Comment comment : commentList) {
            commentResponseDtoList.add(new CommentResponseDto(comment));
        }
        return commentResponseDtoList;
    }

}