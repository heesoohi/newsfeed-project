package com.example.newsfeedproject.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PostSaveRequest {
    @NotBlank(message = "내용을 입력하세요.")
    private String content;
}
