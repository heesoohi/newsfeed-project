package com.example.newsfeedproject.domain.auth.dto;

import lombok.Getter;

@Getter
public class SigninResponse {

    private final Long userId;
    private final String bearerJwt;

    public SigninResponse(Long userId, String bearerJwt) {
        this.userId = userId;
        this.bearerJwt = bearerJwt;
    }
}
