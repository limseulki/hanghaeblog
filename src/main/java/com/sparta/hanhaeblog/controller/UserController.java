package com.sparta.hanhaeblog.controller;
import com.sparta.hanhaeblog.Message.Message;
import com.sparta.hanhaeblog.dto.LoginRequestDto;
import com.sparta.hanhaeblog.dto.SignupRequestDto;
import com.sparta.hanhaeblog.security.UserDetailsImpl;
import com.sparta.hanhaeblog.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class UserController {

    private final UserService userService;

    // 회원가입
    // @Valid. 객체의 제약 조건 검증
    @PostMapping("/signup")
    public Message signup(@Valid SignupRequestDto signupRequestDto) {
        return userService.signup(signupRequestDto);
    }

    // 로그인
    @ResponseBody
    @PostMapping("/login")
    public Message login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        return userService.login(loginRequestDto, response);
    }

    // 탈퇴
    @DeleteMapping("/delete")
    public Message delete(LoginRequestDto loginRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.quit(loginRequestDto, userDetails.getUser());
    }

}
