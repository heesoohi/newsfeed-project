package com.example.newsfeedproject.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommentRequest {

    @NotBlank(message = "내용을 입력하세요.")
    @Size(max = 255)
    private String content;
}
