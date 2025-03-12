package com.example.newsfeedproject.domain.user.dto.request;

import com.example.newsfeedproject.common.Const;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserWithdrawRequest {
    @NotBlank(message = "이전 비밀번호 입력은 필수입니다.")
    @Size(min = 8, max = 50)
    @Pattern(
            regexp = Const.PASSWORD_PATTERN,
            message = "비밀번호 형식이 올바르지 않습니다."
    )
    private String password;
}
