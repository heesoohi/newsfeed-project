package com.example.newsfeedproject.domain.user.dto;

import lombok.Getter;

@Getter
public class UserResponse {

    private final Long userId;
    private final String email;
    private final String username;
    private final int followerCount;
    private final int followingCount;

    public UserResponse(Long userId, String email, String username, int followerCount, int followingCount) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
    }
}
