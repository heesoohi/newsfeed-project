package com.example.newsfeedproject.domain.auth.dto;

import com.example.newsfeedproject.common.Const;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class SignupRequest {

    @Email
    @NotBlank(message = "이메일 입력은 필수입니다.")
    @Size(max = 255)
    @Pattern(
            regexp = Const.EMAIL_PATTERN,
            message = "이메일 형식이 올바르지 않습니다."
    )
    private String email;

    @NotBlank(message = "유저이름 입력은 필수입니다.")
    @Size(max = 50)
    private String username;

    @NotBlank(message = "비밃번호 입력은 필수입니다.")
    @Size(min = 8, max = 50)
    @Pattern(
            regexp = Const.PASSWORD_PATTERN,
            message = "비밀번호 형식이 올바르지 않습니다."
    )
    private String password;
}
