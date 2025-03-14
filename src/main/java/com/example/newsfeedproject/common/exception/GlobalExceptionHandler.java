package com.example.newsfeedproject.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class) //어떤 익셉션 클래스를 처리하는건지
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) { // CustomeException이 발생하면 처리할 로직 정의
        log.error("Custom Exception [statusCode = {}, msg = {}]",
                e.getHttpStatus(), e.getMessage());
        return ResponseEntity.status(e.getHttpStatus())
                .body(ErrorResponse.from(e));
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid( /* @Valid 검증 오류 */
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        ExceptionType exceptionType = ExceptionType.INVALID_REQUEST;

        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> "["+error.getField()+"] " + error.getDefaultMessage())
                .toList();

        log.error("Validation Exception [uri = {}, msg = {}]",
                request.getContextPath(), e.getMessage());
        return ResponseEntity.status(exceptionType.getHttpStatus())
                .body(new ErrorResponse(exceptionType, String.join(", ",errors)));
    }
}
