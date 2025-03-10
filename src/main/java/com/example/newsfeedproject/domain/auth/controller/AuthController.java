package com.example.newsfeedproject.domain.auth.controller;

import com.example.newsfeedproject.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

//    @PostMapping("/auth/signup")
//    public ResponseEntity<void> signup(@RequestBody )
}
