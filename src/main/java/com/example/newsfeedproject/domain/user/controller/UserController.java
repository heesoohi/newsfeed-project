package com.example.newsfeedproject.domain.user.controller;

import com.example.newsfeedproject.common.annotation.Auth;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.user.dto.request.UserPasswordUpdateRequest;
import com.example.newsfeedproject.domain.user.dto.request.UserUpdateRequest;
import com.example.newsfeedproject.domain.user.dto.request.UserWithdrawRequest;
import com.example.newsfeedproject.domain.user.dto.response.UserResponse;
import com.example.newsfeedproject.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 유저 정보 단건 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser (@PathVariable Long userId){
        return ResponseEntity.ok(userService.getUser(userId));
    }

    // 본인 프로필 수정
    @PutMapping
    public ResponseEntity<Void> updateUser (
            @Auth AuthUser authUser,
            @Valid @RequestBody UserUpdateRequest dto
    ) {
        userService.updateUser(authUser, dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword (
            @Auth AuthUser authUser,
            @Valid @RequestBody UserPasswordUpdateRequest dto
    ) {
        userService.updatePassword(authUser, dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdrawUser (
            @Auth AuthUser authUser,
            @Valid @RequestBody UserWithdrawRequest dto
    ) {
        userService.withdraw(authUser, dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
