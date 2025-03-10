package com.example.newsfeedproject.domain.user.service;

import com.example.newsfeedproject.domain.auth.dto.SignupRequest;
import com.example.newsfeedproject.domain.user.dto.UserSaveResponse;
import com.example.newsfeedproject.domain.user.entity.User;
import com.example.newsfeedproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserSaveResponse save(SignupRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("이미 가입된 이메일 입니다.");
        }

        User user = new User(dto.getEmail(), dto.getUsername(), dto.getPassword());
        User savedUser = userRepository.save(user);

        return new UserSaveResponse(savedUser.getUserId());
    }
}
