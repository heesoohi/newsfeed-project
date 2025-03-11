package com.example.newsfeedproject.domain.auth.controller;

import com.example.newsfeedproject.domain.auth.dto.SigninRequest;
import com.example.newsfeedproject.domain.auth.dto.SigninResponse;
import com.example.newsfeedproject.domain.auth.dto.SignupRequest;
import com.example.newsfeedproject.domain.auth.service.AuthService;
import com.example.newsfeedproject.domain.user.dto.response.UserSaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<UserSaveResponse> signup(@RequestBody SignupRequest dto) {
        return ResponseEntity.ok(authService.signup(dto));
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<SigninResponse> signin(@RequestBody SigninRequest dto) {
        SigninResponse signinResponse = authService.signin(dto);

        return ResponseEntity.ok()
                .header("Authorization", signinResponse.getBearerJwt())
                .body(signinResponse);
    }
}
