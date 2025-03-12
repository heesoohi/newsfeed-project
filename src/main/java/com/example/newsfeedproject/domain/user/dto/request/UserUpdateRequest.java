package com.example.newsfeedproject.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdateRequest {
    @NotBlank(message = "유저이름 입력은 필수입니다.")
    @Size(max = 50)
    private String username;
}
