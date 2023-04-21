package com.sparta.hanhaeblog.service;

import com.sparta.hanhaeblog.Exception.CustomException;
import com.sparta.hanhaeblog.dto.CommentResponseDto;
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

import static com.sparta.hanhaeblog.Exception.ErrorCode.*;

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
                () -> new CustomException(POST_NOT_FOUND)
        );
        return new PostResponseDto(post, getCommentList(id));
    }

    // 선택한 Post 수정
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
        // 토큰 체크
        User user = checkJwtToken(request);

        Post post = postRepository.findById(id).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );

        UserRoleEnum userRoleEnum = user.getRole();

        if(userRoleEnum == UserRoleEnum.ADMIN) {
            post.update(requestDto);
            return new PostResponseDto(post, getCommentList(id));
        } else {
            if(post.getUsername() != user.getUsername()) {
                throw new CustomException(AUTHOR_NOT_SAME_MOD);
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
                () -> new CustomException(POST_NOT_FOUND)
        );

        UserRoleEnum userRoleEnum = user.getRole();

        if(userRoleEnum == UserRoleEnum.ADMIN) {
            commentRepository.deleteAllByPostId(id);
            postRepository.delete(post);
            return "게시글 삭제 성공";
        } else {
            if(post.getUsername() != user.getUsername()) {
                throw new CustomException(AUTHOR_NOT_SAME_DEL);
            }
            commentRepository.deleteAllByPostId(id);
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
                    () -> new CustomException(CANNOT_FOUND_USER)
            );
            return user;

        }
        return null;
    }

    private List<CommentResponseDto> getCommentList(Long postId) {
        List<Comment> commentList = commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        for(Comment comment : commentList) {
            commentResponseDtoList.add(new CommentResponseDto(comment));
        }
        return commentResponseDtoList;
    }

}