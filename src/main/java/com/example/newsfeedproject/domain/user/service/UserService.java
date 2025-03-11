package com.example.newsfeedproject.domain.user.service;

import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.user.dto.request.UserUpdateRequest;
import com.example.newsfeedproject.domain.user.dto.response.UserFindByEmailResponse;
import com.example.newsfeedproject.domain.user.dto.response.UserResponse;
import com.example.newsfeedproject.domain.user.dto.response.UserSaveResponse;
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

    public void updateUser(AuthUser authUser, UserUpdateRequest dto) {
        User user = userRepository.findById(authUser.getUserId()).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );

        user.update(dto.getUsername());
    }
}
