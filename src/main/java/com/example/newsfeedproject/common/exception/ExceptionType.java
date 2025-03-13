package com.example.newsfeedproject.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
public enum ExceptionType {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청값 검증에 실패했습니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "해당 이메일로 가입한 계정이 존재합니다."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "해당 이메일로 등록된 계정을 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "입력된 비밀번호가 틀렸습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    SAME_AS_OLD_PASSWORD(CONFLICT, "기존 비밀번호와 새 비밀번호가 같으면 안 됩니다."),
    ALREADY_DELETED_USER(UNAUTHORIZED, "이미 탈퇴한 사용자입니다."),
    NO_PERMISSION_ACTION(HttpStatus.FORBIDDEN, "권한이 없는 작업입니다."),
    ALREADY_UNFOLLOWED(HttpStatus.NO_CONTENT, "요청이 정상적으로 처리되었지만, 해당 유저는 이미 팔로우 상태가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
