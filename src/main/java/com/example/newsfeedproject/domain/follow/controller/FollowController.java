package com.example.newsfeedproject.domain.follow.controller;

import com.example.newsfeedproject.common.annotation.Auth;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{targetUserId}")
    public ResponseEntity<Void> follow(
            @Auth AuthUser authUser,
            @PathVariable Long targetUserId
            ) {
        followService.follow(authUser, targetUserId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<Void> unFollow(
            @Auth AuthUser authUser,
            @PathVariable Long targetUserId
    ) {
        followService.unfollow(authUser, targetUserId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
