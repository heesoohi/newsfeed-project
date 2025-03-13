package com.example.newsfeedproject.domain.follow.controller;

import com.example.newsfeedproject.common.annotation.Auth;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow/{targetUserId}")
    public ResponseEntity<Void> follow(
            @Auth AuthUser authUser,
            @PathVariable Long targetUserId
            ) {
        followService.follow(authUser, targetUserId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
