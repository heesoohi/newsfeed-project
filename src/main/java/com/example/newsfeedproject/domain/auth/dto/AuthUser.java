package com.example.newsfeedproject.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthUser {
    private Long userId;
    private String email;

    @Builder
    public AuthUser(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }
}
