package com.sparta.hanhaeblog.controller;

import com.sparta.hanhaeblog.Exception.CustomException;
import com.sparta.hanhaeblog.Message.Message;
import com.sparta.hanhaeblog.dto.PostRequestDto;
import com.sparta.hanhaeblog.dto.PostResponseDto;
import com.sparta.hanhaeblog.security.UserDetailsImpl;
import com.sparta.hanhaeblog.service.PostService;
import com.sparta.hanhaeblog.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sparta.hanhaeblog.Exception.ErrorCode.CANNOT_FOUND_USER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;
    private final SearchService searchService;

    // 전체 게시글 조회
    @GetMapping("/posts")
    public List<PostResponseDto> getPosts(@RequestParam("page") int page,
                                          @RequestParam("size") int size,
                                          @RequestParam("sortBy") String sortBy,
                                          @RequestParam("isAsc") boolean isAsc) {
        return postService.getPosts(page-1, size, sortBy, isAsc);
    }

    // 선택한 게시글 조회
    // @PathVariable. uri를 통해 전달된 값을 파라미터로 받아옴. 쿼드스트링으로 전달되는 경우 외의 uri
    @GetMapping("/post/{id}")
    public PostResponseDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    // 게시글 등록
    // @RequestBody. http 요청의 본문 전달. json 기반의 메시지 사용하는 요청의 경우 필요함
    @PostMapping("/posts")
    public PostResponseDto createPost(@RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.createPost(requestDto, userDetails.getUser());
    }

    // 게시글 수정
    // @AuthenticationPrincipal. 인증객체의 principal 부분의 값을 가져온다
    @PutMapping("/post/{id}")
    public PostResponseDto updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.updatePost(id, requestDto, userDetails.getUser());
    }

    // 게시글 삭제
    @DeleteMapping("/post/{id}")
    public Message deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.deletePost(id, userDetails.getUser());
    }

    // 게시글 검색
    @GetMapping("/post/search")
    public List<PostResponseDto> searchPost(@RequestParam String keyword) {
        return searchService.searchPost(keyword);
    }
}
