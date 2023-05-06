package com.sparta.hanhaeblog.controller;

import com.sparta.hanhaeblog.Exception.CustomException;
import com.sparta.hanhaeblog.Message.Message;
import com.sparta.hanhaeblog.dto.ImageDto;
import com.sparta.hanhaeblog.dto.PostRequestDto;
import com.sparta.hanhaeblog.dto.PostResponseDto;
import com.sparta.hanhaeblog.security.UserDetailsImpl;
import com.sparta.hanhaeblog.service.PostService;
import com.sparta.hanhaeblog.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
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

    // 사진 등록
    @PostMapping("/post/image")
    public Message imageUpload(@RequestParam(name = "image")MultipartFile image, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        // 폴더 생성과 파일명 새로 부여를 위한 현재 시간 알아내기
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);

        // 파일이 저장될 절대 경로
        String absolutePath = new File("/Users/gram/Desktop").getAbsolutePath() + "/";
        // 새로 부여한 이미지명
        String newFileName = "image" + hour + minute + second + millis;
        // 정규식 이용하여 확장자만 추출
        String fileExtension = '.' + image.getOriginalFilename().replaceAll("^.*\\.(.*)$", "$1");
        // 저장될 폴더 경로
        String path = "images/test/" + year + "_" + month + "_" + day;

        try {
            if(!image.isEmpty()) {
                File file = new File(absolutePath + path);
                if(!file.exists()){
                    // mkdir()과 다르게 상위 폴더가 없을 때 상위폴더까지 생성
                    file.mkdirs();
                }

                file = new File(absolutePath + path + "/" + newFileName + fileExtension);
                image.transferTo(file);

                ImageDto imgDto = ImageDto.builder()
                        .originImageName(image.getOriginalFilename())
                        .imagePath(path)
                        .imageName(newFileName + fileExtension)
                        .build();

                postService.saveImage(imgDto, userDetails.getUser());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message("사진 등록 성공", 200);
    }
}
