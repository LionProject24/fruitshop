package com.lionproject24.fruitshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 우리가 던진 에러 (아이디 중복, 유저 없음, 비밀번호 틀림 등)
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException e) {
        ErrorCode code = e.getErrorCode();
        return ResponseEntity.status(code.getStatus())
                .body(Map.of("code", code.name(), "message", code.getMessage()));
    }

    // @Valid 검증 실패 (빈 값 등)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(Map.of("code", ErrorCode.INVALID_INPUT.name(), "message", message));
    }

    // 그 외 예상 못한 모든 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("code", ErrorCode.UNKNOWN_ERROR.name(), "message", ErrorCode.UNKNOWN_ERROR.getMessage()));
    }
}
