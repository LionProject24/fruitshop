package com.lionproject24.fruitshop.exception;

import io.jsonwebtoken.JwtException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
public enum ErrorCode {

    //    Auth
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),

    // JWT 토큰
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 처리 중 알 수 없는 오류가 발생했습니다."),
    NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED,"요청 헤더에서 토큰을 찾을 수 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"기간이 만료된 토큰입니다. 다시 로그인해주세요."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED,"지원하지 않는 토큰 형식입니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "요청 값 검증에 실패했습니다.");

    // Product
//    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
//    PRODUCT_FORBIDDEN(HttpStatus.FORBIDDEN, "본인 상품만 수정/삭제 가능합니다."),

    // Cart
//    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니가 없습니다."),
//    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니 상품을 찾을 수 없습니다."),
//    CART_FORBIDDEN(HttpStatus.FORBIDDEN, "본인 장바구니만 수정/삭제 가능합니다.")

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ErrorCode findByCode(String code){
        return Arrays.stream(ErrorCode.values())    // enum 값 전체를 스트림으로
                .filter(c ->c.name().equals(code)) //code가 일치하는 것만
                .findFirst() // 그중 첫번째 것을 꺼냄
                .orElse(UNKNOWN_ERROR);    // 없으면 UNKNOWN_ERROR로 대체
    }
}
