package com.example.newsfeedproject.domain.auth.service;

import com.example.newsfeedproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
}
