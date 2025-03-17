package com.example.newsfeedproject.domain.user.service;

import com.example.newsfeedproject.common.config.PasswordEncoder;
import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.domain.user.dto.response.UserFindByEmailResponse;
import com.example.newsfeedproject.domain.user.dto.response.UserResponse;
import com.example.newsfeedproject.domain.user.dto.response.UserSaveResponse;
import com.example.newsfeedproject.domain.user.entity.User;
import com.example.newsfeedproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @DisplayName("유저가 정상적으로 저장된다.")
    @Test
    void save1() {
        // given
        String email = "test@test.com";
        String username = "name";
        String encodedPassword = "encodedPassword";
        Long saveUserId = 1L;

        User savedUser = mock(User.class);
        given(savedUser.getUserId()).willReturn(saveUserId);
        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        UserSaveResponse response = userService.save(email, username, encodedPassword);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(saveUserId);

        verify(userRepository, times(1)).existsByEmail(email);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("중복된 이메일로 유저 저장 시 DUPLICATE_EMAIL Exception 을 던진다.")
    void saveWithDuplicateEmail() {
        // given
        String email = "test@test.com";
        String username = "name";
        String encodedPassword = "encodedPassword";

        given(userRepository.existsByEmail(email)).willReturn(true);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.save(email, username, encodedPassword),
                "DUPLICATE_EMAIL expected"
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.DUPLICATE_EMAIL);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.BAD_REQUEST);
        assertThat(exception.getMessage()).isEqualTo("해당 이메일로 가입한 계정이 존재합니다.");

        verify(userRepository, times(1)).existsByEmail(email);
    }

    @DisplayName("이메일로 유저를 정상적으로 조회한다.")
    @Test
    void findByEmailSuccess() {
        // given
        String email = "test@test.com";
        Long userId = 1L;
        String encodedPassword = "encodedPassword";

        User user = mock(User.class);
        given(user.getUserId()).willReturn(userId);
        given(user.getEmail()).willReturn(email);
        given(user.getPassword()).willReturn(encodedPassword);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // when
        UserFindByEmailResponse foundUser = userService.findByEmail(email);

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserId()).isEqualTo(userId);
        assertThat(foundUser.getEmail()).isEqualTo(email);
        assertThat(foundUser.getPassword()).isEqualTo(encodedPassword);

        verify(userRepository, times(1)).findByEmail(email);
    }

    @DisplayName("존재하지 않는 이메일로 조회 시 INVALID_EMAIL Exception 을 던진다.")
    @Test
    void findByEmailWithInvalidEmail() {
        // given
        String email = "test@test.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.findByEmail(email),
                "INVALID_EMAIL expected"
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.INVALID_EMAIL);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.BAD_REQUEST);
        assertThat(exception.getMessage()).isEqualTo("해당 이메일로 등록된 계정을 찾을 수 없습니다.");

        verify(userRepository, times(1)).findByEmail(email);
    }

    @DisplayName("유저 ID로 유저를 정상적으로 조회한다.")
    @Test
    void getUserSuccess() {
        // given
        Long userId = 1L;
        String email = "test@test.com";
        String username = "name";
        int followerCount = 10;
        int followingCount = 5;

        User user = mock(User.class);
        given(user.getUserId()).willReturn(userId);
        given(user.getEmail()).willReturn(email);
        given(user.getUsername()).willReturn(username);
        given(user.getFollowerCount()).willReturn(followerCount);
        given(user.getFollowingCount()).willReturn(followingCount);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        UserResponse response = userService.getUser(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getUsername()).isEqualTo(username);
        assertThat(response.getFollowerCount()).isEqualTo(followerCount);
        assertThat(response.getFollowingCount()).isEqualTo(followingCount);

        verify(userRepository, times(1)).findById(userId);
    }

    @DisplayName("존재하지 않는 유저 ID로 조회 시 USER_NOT_FOUND Exception 을 던진다.")
    @Test
    void getUserWithNotFound() {
        // given
        Long userId = -1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.getUser(userId),
                "USER_NOT_FOUND expected"
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.USER_NOT_FOUND);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("해당 사용자를 찾을 수 없습니다.");

        verify(userRepository, times(1)).findById(userId);
    }

}
