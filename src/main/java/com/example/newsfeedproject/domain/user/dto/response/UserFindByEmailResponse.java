package com.example.newsfeedproject.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserFindByEmailResponse {

    private final Long userId;
    private final String email;
    private final String password;

    @Builder
    public UserFindByEmailResponse(Long userId, String email, String password) {

        this.userId = userId;
        this.email = email;
        this.password = password;
    }
}
