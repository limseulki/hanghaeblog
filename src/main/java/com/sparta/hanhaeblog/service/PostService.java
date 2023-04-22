package com.sparta.hanhaeblog.service;

import com.sparta.hanhaeblog.Exception.CustomException;
import com.sparta.hanhaeblog.Message.Message;
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
    public PostResponseDto createPost(PostRequestDto requestDto, User user) {

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
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto, User user) {

        UserRoleEnum userRoleEnum = user.getRole();

        Post post;
        if(userRoleEnum == UserRoleEnum.ADMIN) {
            post = postRepository.findById(id).orElseThrow(
                    () -> new CustomException(POST_NOT_FOUND)
            );
        } else {
            post = postRepository.findByIdAndUsername(id, user.getUsername()).orElseThrow(
                    () -> new CustomException(AUTHOR_NOT_SAME_MOD)
            );
        }
        post.update(requestDto);
        return new PostResponseDto(post, getCommentList(id));
    }

    // 선택한 Post 삭제
    @Transactional
    public Message deletePost(Long id, User user) {

        UserRoleEnum userRoleEnum = user.getRole();

        Post post;
        if(userRoleEnum == UserRoleEnum.ADMIN) {
            post = postRepository.findById(id).orElseThrow(
                    () -> new CustomException(POST_NOT_FOUND)
            );
        } else {
            postRepository.findByIdAndUsername(id, user.getUsername()).orElseThrow(
                    () -> new CustomException(AUTHOR_NOT_SAME_DEL)
            );
        }

        commentRepository.deleteAllByPostId(id);
        postRepository.deleteById(id);
        return new Message("게시글 삭제 성공", 200);
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