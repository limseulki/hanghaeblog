package com.sparta.hanhaeblog.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    POST_NOT_FOUND(BAD_REQUEST, "게시글이 존재하지 않습니다."),
    COMMENT_NOT_FOUND(BAD_REQUEST, "해당 댓글이 존재하지 않습니다."),
    CANNOT_FOUND_USERNAME(BAD_REQUEST, "사용자가 존재하지 않습니다."),

    AUTHOR_NOT_SAME_MOD(BAD_REQUEST, "작성자만 수정할 수 있습니다."),
    AUTHOR_NOT_SAME_DEL(BAD_REQUEST, "작성자만 삭제할 수 있습니다."),

    INVALIDATED_TOKEN(BAD_REQUEST, "토큰이 유효하지 않습니다."),

    NOT_MATCH_ADMIN_TOKEN(BAD_REQUEST, "관리자 암호가 틀려 등록이 불가능합니다."),

    CANNOT_FOUND_USER(BAD_REQUEST, "회원을 찾을 수 없습니다."),
    EXIST_USERNAME(BAD_REQUEST, "중복된 username 입니다.");


    private final HttpStatus httpStatus;
    private final String detail;
}
