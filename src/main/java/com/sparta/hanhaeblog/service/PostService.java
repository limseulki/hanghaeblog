package com.sparta.hanhaeblog.service;

import com.sparta.hanhaeblog.Exception.CustomException;
import com.sparta.hanhaeblog.Message.Message;
import com.sparta.hanhaeblog.dto.CommentResponseDto;
import com.sparta.hanhaeblog.dto.PostRequestDto;
import com.sparta.hanhaeblog.dto.PostResponseDto;
import com.sparta.hanhaeblog.entity.*;
import com.sparta.hanhaeblog.repository.CommentRepository;
import com.sparta.hanhaeblog.repository.LikeRepository;
import com.sparta.hanhaeblog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.sparta.hanhaeblog.Exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    Post post;

    // 게시글 작성
    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto, User user) {
        List<CommentResponseDto> commentList = new ArrayList<>();
        post = postRepository.saveAndFlush(new Post(requestDto, user.getUsername()));
        return new PostResponseDto(post, commentList);
    }

    // 전체 게시글 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts() {
        // 게시글 작성일 기준 내림차순으로 찾아오기
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostResponseDto> postList = new ArrayList<>();
        for(Post post : posts) {
            postList.add(new PostResponseDto(post, getCommentList(post.getId())));
        }
        return postList;
    }

    // 선택한 게시글 조회
    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long id) {
        post = existPost(id);
        return new PostResponseDto(post, getCommentList(id));
    }

    // 선택한 게시글 수정
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto, User user) {
        post = checkRole(id, user);
        post.update(requestDto);
        return new PostResponseDto(post, getCommentList(id));
    }

    // 선택한 게시글 삭제
    @Transactional
    public Message deletePost(Long id, User user) {
        post = checkRole(id, user);
        // 게시글에 달린 댓글의 좋아요 삭제
        // 1. 게시글에 달린 댓글 Id(commentId) 찾기
        List<Comment> commentList= commentRepository.findAllByPostIdOrderByCreatedAtDesc(id);
        List<Long> commentIdList = new ArrayList<>();
        for (Comment comment : commentList) {
            commentIdList.add(comment.getId());
        }
        // 2. like 에서 삭제
        for (Long commentId : commentIdList) {
            likeRepository.deleteAllByCommentId(commentId);
        }
        // 게시글 좋아요 삭제
        likeRepository.deleteAllByPostId(id);
        // 게시글에 달린 댓글 전체 삭제
        commentRepository.deleteAllByPostId(id);
        // 그 후 게시글 삭제
        postRepository.deleteById(id);
        return new Message("게시글 삭제 성공", 200);
    }

    // 게시글에 달린 댓글 가져오기
    private List<CommentResponseDto> getCommentList(Long postId) {
        // 게시글에 달린 댓글 찾아서 작성일 기준 내림차순 정렬
        List<Comment> commentList = commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        for(Comment comment : commentList) {
            commentResponseDtoList.add(new CommentResponseDto(comment));
        }
        return commentResponseDtoList;
    }

    // 게시글 DB 유무 확인
    private Post existPost(Long id) {
        post = postRepository.findById(id).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );
        return post;
    }

    // 작성자 일치 여부 확인
    private Post matchAuthor(Long id, User user) {
        post = postRepository.findByIdAndUsername(id, user.getUsername()).orElseThrow(
                () -> new CustomException(AUTHOR_NOT_SAME_MOD)
        );
        return post;
    }

    // 관리자 여부 확인
    private Post checkRole(Long id, User user) {
        UserRoleEnum userRoleEnum = user.getRole();
        if(userRoleEnum == UserRoleEnum.ADMIN) {
            post = existPost(id);
        } else {
            post = matchAuthor(id, user);
        }
        return post;
    }

}