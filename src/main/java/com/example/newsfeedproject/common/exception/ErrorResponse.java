package com.example.newsfeedproject.common.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private ExceptionType type;
    private String message;

    public ErrorResponse(ExceptionType type, String message) {
        this.type = type;
        this.message = message;
    }

    public static ErrorResponse from(CustomException e) {
        return new ErrorResponse(e.getExceptionType(), e.getMessage());
    }
}
