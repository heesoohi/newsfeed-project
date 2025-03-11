package com.example.newsfeedproject.domain.user.service;

import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.domain.auth.dto.SignupRequest;
import com.example.newsfeedproject.domain.user.dto.UserFindByEmailResponse;
import com.example.newsfeedproject.domain.user.dto.UserResponse;
import com.example.newsfeedproject.domain.user.dto.UserSaveResponse;
import com.example.newsfeedproject.domain.user.entity.User;
import com.example.newsfeedproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserSaveResponse save(String email, String username, String encodedPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ExceptionType.DUPLICATE_EMAIL);
        }

        User user = new User(email, username, encodedPassword);
        User savedUser = userRepository.save(user);

        return new UserSaveResponse(savedUser.getUserId());
    }

    public UserFindByEmailResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ExceptionType.INVALID_EMAIL)
        );

        return new UserFindByEmailResponse(user.getUserId(), user.getEmail(), user.getPassword());
    }

    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );

        return new UserResponse(user.getUserId(), user.getEmail(), user.getUsername(), user.getFollowerCount(), user.getFollowingCount());
    }
}
