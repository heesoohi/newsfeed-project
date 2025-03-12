package com.example.newsfeedproject.domain.user.service;

import com.example.newsfeedproject.common.config.PasswordEncoder;
import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.user.dto.request.UserPasswordUpdateRequest;
import com.example.newsfeedproject.domain.user.dto.request.UserUpdateRequest;
import com.example.newsfeedproject.domain.user.dto.request.UserWithdrawRequest;
import com.example.newsfeedproject.domain.user.dto.response.UserFindByEmailResponse;
import com.example.newsfeedproject.domain.user.dto.response.UserResponse;
import com.example.newsfeedproject.domain.user.dto.response.UserSaveResponse;
import com.example.newsfeedproject.domain.user.entity.User;
import com.example.newsfeedproject.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserSaveResponse save(String email, String username, String encodedPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ExceptionType.DUPLICATE_EMAIL);
        }

        User user = new User(email, username, encodedPassword);
        User savedUser = userRepository.save(user);

        return new UserSaveResponse(savedUser.getUserId());
    }

    @Transactional(readOnly = true)
    public UserFindByEmailResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(ExceptionType.INVALID_EMAIL)
        );

        return new UserFindByEmailResponse(user.getUserId(), user.getEmail(), user.getPassword());
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );

        return new UserResponse(user.getUserId(), user.getEmail(), user.getUsername(), user.getFollowerCount(), user.getFollowingCount());
    }

    @Transactional
    public void updateUser(AuthUser authUser, UserUpdateRequest dto) {
        System.out.println("2222222222");
        User user = userRepository.findById(authUser.getUserId()).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );

        user.update(dto.getUsername());
    }

    @Transactional
    public void updatePassword(AuthUser authUser, UserPasswordUpdateRequest dto) {
        User user = userRepository.findById(authUser.getUserId()).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new CustomException(ExceptionType.INVALID_PASSWORD);
        }

        if (dto.getNewPassword().equals(dto.getOldPassword())) {
            throw new CustomException(ExceptionType.SAME_AS_OLD_PASSWORD);
        }

        user.upadtePassword(passwordEncoder.encode(dto.getNewPassword()));
    }

    @Transactional
    public void withdraw(AuthUser authUser, @Valid UserWithdrawRequest dto) {
        User user = userRepository.findById(authUser.getUserId()).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );

        if (user.getDeletedAt() != null) {
            throw new CustomException(ExceptionType.ALREADY_DELETED_USER);
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CustomException(ExceptionType.INVALID_PASSWORD);
        }

        user.delete();
    }
}
