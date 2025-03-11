package com.example.newsfeedproject.domain.user.controller;

import com.example.newsfeedproject.domain.user.dto.UserResponse;
import com.example.newsfeedproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 유저 정보 단건 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser (@PathVariable Long userId){
        return ResponseEntity.ok(userService.getUser(userId));
    }
}
