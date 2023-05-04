package com.sparta.hanhaeblog.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // CustomException 클래스에서 예외 핸들러
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    // Valid 예외 핸들러 (아이디 패스워드 유효성 검사)
    @ExceptionHandler({BindException.class})
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();

        StringBuilder sb = new StringBuilder();
        for ( FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getDefaultMessage());
            sb.append(", ");
        }
        return ErrorResponse.toResponseEntityValid(sb.toString(), HttpStatus.BAD_REQUEST);
    }

    // NullpointerException 클래스 예외 핸들러
    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
        return ErrorResponse.toResponseEntityValid(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
