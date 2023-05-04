package com.sparta.hanhaeblog.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.hanhaeblog.Exception.CustomException;
import com.sparta.hanhaeblog.dto.SecurityExceptionDto;
import com.sparta.hanhaeblog.entity.User;
import com.sparta.hanhaeblog.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String access_token = jwtUtil.resolveToken(request, jwtUtil.ACCESS_KEY);
        String refresh_token = jwtUtil.resolveToken(request, jwtUtil.REFRESH_KEY);


        if(access_token != null) {
            if(jwtUtil.validateToken(access_token)) {      // 토큰 검증
                setAuthentication(jwtUtil.getUserInfoFromToken(access_token));
            } else if(refresh_token != null && jwtUtil.refreshTokenValidation(refresh_token)) {
                String username = jwtUtil.getUserInfoFromToken(refresh_token);
                User user = userRepository.findByUsername(username).get();
                String newAccessToken = jwtUtil.createToken(username, user.getRole(), "Access");
                jwtUtil.setHeaderAccessToken(response, newAccessToken);
                setAuthentication(username);
            } else if(refresh_token == null) {
                jwtExceptionHandler(response, "AccessToken Expired", HttpStatus.UNAUTHORIZED.value());
                return;
            } else {
                jwtExceptionHandler(response, "RefreshToken Expired", HttpStatus.UNAUTHORIZED.value());
                return;
            }
        } else {
            jwtExceptionHandler(response, "AccessToken is Empty", HttpStatus.BAD_REQUEST.value());
        }
        filterChain.doFilter(request, response);    // 다음 filter로 넘어가기
    }

    // 스프링 시큐리티로 인증한 사용자의 상세 정보 저장
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 예외 핸들러
    public void jwtExceptionHandler(HttpServletResponse response, String msg, int statusCode) {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString(new SecurityExceptionDto(statusCode, msg));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}