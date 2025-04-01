package com.example.newsfeedproject.domain.auth.service;

import com.example.newsfeedproject.common.config.PasswordEncoder;
import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.common.utils.JwtUtil;
import com.example.newsfeedproject.domain.auth.dto.SigninRequest;
import com.example.newsfeedproject.domain.auth.dto.SigninResponse;
import com.example.newsfeedproject.domain.auth.dto.SignupRequest;
import com.example.newsfeedproject.domain.user.dto.response.UserFindByEmailResponse;
import com.example.newsfeedproject.domain.user.dto.response.UserSaveResponse;
import com.example.newsfeedproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserSaveResponse signup(SignupRequest dto) {

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        return userService.save(dto.getEmail(), dto.getUsername(), encodedPassword);
    }

    @Transactional(readOnly = true)
    public SigninResponse signin(SigninRequest dto) {
        UserFindByEmailResponse userResult = userService.findByEmail(dto.getEmail());

//        System.out.println("dto password: " + dto.getPassword());
//        System.out.println("user password: " + userResult.getPassword());

        String bearerJwt = jwtUtil.createToken(userResult.getUserId(), userResult.getEmail());

        return new SigninResponse(userResult.getUserId(), bearerJwt);
    }
}
