package com.example.newsfeedproject.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {

    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "해당 이메일로 가입한 계정이 존재합니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
