package com.sparta.hanhaeblog.Exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorResponse {
    private final String message;
    private final int status;

    // 에러 반환 형식
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .message(errorCode.getDetail())
                        .status(errorCode.getHttpStatus().value())
                        .build()
                );
    }
}
