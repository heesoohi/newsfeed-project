package com.example.newsfeedproject.domain.auth.service;

import com.example.newsfeedproject.domain.auth.dto.SignupRequest;
import com.example.newsfeedproject.domain.user.dto.UserSaveResponse;
import com.example.newsfeedproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    public UserSaveResponse signup(SignupRequest dto) {
        return userService.save(dto);
    }
}
