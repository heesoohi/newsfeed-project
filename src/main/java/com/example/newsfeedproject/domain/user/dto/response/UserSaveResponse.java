package com.example.newsfeedproject.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSaveResponse {

    private final Long userId;

    @Builder
    public UserSaveResponse(Long userId) {
        this.userId = userId;
    }
}
