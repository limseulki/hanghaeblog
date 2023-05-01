package com.sparta.hanhaeblog.service;

import com.sparta.hanhaeblog.Exception.CustomException;
import com.sparta.hanhaeblog.Message.Message;
import com.sparta.hanhaeblog.dto.LoginRequestDto;
import com.sparta.hanhaeblog.dto.SignupRequestDto;
import com.sparta.hanhaeblog.dto.TokenDto;
import com.sparta.hanhaeblog.entity.RefreshToken;
import com.sparta.hanhaeblog.entity.User;
import com.sparta.hanhaeblog.entity.UserRoleEnum;
import com.sparta.hanhaeblog.jwt.JwtUtil;
import com.sparta.hanhaeblog.repository.RefreshTokenRepository;
import com.sparta.hanhaeblog.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.sparta.hanhaeblog.Exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    // ADMIN_TOKEN
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    @Transactional
    public Message signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        // 비밀번호 암호화 해서 저장
        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        // 회원 중복 확인
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new CustomException(EXIST_USERNAME);
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (signupRequestDto.isAdmin()) {
            if (!signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                    throw new CustomException(NOT_MATCH_ADMIN_TOKEN);
            }
            role = UserRoleEnum.ADMIN;
        }
        User user = new User(username, password, role);
        userRepository.save(user);
        return new Message("회원가입 성공", 200);
    }

    @Transactional
    public Message login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(CANNOT_FOUND_USER)
        );

        // 비밀번호 확인. (사용자가 입력한 비밀번호, 저장된 비밀번호)
        // 사용자가 입력한 비밀번호를 암호화해서 DB에 저장된 비밀번호와 비교하여 인증
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw  new CustomException(CANNOT_FOUND_USER);
        }

        TokenDto tokenDto = jwtUtil.createAllToken(username, user.getRole());

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUsername(username);

        if(refreshToken.isPresent()) {
            RefreshToken updateToken = refreshToken.get().updateToken(tokenDto.getRefreshToken().substring(7));
            refreshTokenRepository.save(updateToken);
            user.update(updateToken);
        } else {
            RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken().substring(7), username, user);
            refreshTokenRepository.save(newToken);
            user.update(newToken);
        }

        response.addHeader(jwtUtil.ACCESS_KEY, tokenDto.getAccessToken());
        response.addHeader(jwtUtil.REFRESH_KEY, tokenDto.getRefreshToken());

        // Header에 토큰 저장
        return new Message("로그인 성공", 200);
    }

    @Transactional
    public Message quit(LoginRequestDto loginRequestDto, User user) {
        String password = loginRequestDto.getPassword();

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw  new CustomException(CANNOT_FOUND_USER);
        }

        userRepository.deleteById(user.getId());
        return new Message("탈퇴 성공", 200);
    }

}
