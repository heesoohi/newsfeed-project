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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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

        User user = new User(email, username, encodedPassword);
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
        Long userId = 1L;
        String email = "test@test.com";
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

    @DisplayName("유저 정보가 정상적으로 업데이트된다.")
    @Test
    void updateUserSuccess() {
        // given
        Long userId = 1L;
        String email = "test@test.com";
        String initialUsername = "oldName";
        String newUsername = "newName";
        String encodedPassword = "encodedPassword";

        AuthUser authUser = new AuthUser(userId, email);
        UserUpdateRequest dto = new UserUpdateRequest(newUsername);
        User user = new User(email, initialUsername, encodedPassword);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userService.updateUser(authUser, dto);

        // then
        assertThat(user.getUsername()).isEqualTo(newUsername);
        verify(userRepository, times(1)).findById(userId);
    }

    @DisplayName("존재하지 않는 유저 ID로 업데이트 시 USER_NOT_FOUND 예외를 던진다.")
    @Test
    void updateUserWithNotFound() {
        // given
        Long userId = -1L;
        String email = "test@test.com";
        String newUsername = "newName";

        AuthUser authUser = new AuthUser(userId, email);
        UserUpdateRequest dto = new UserUpdateRequest(newUsername);
        given(userRepository.findById(userId)).willReturn(Optional.empty());
        
        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.updateUser(authUser, dto),
                "USER_NOT_FOUND expected"
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.USER_NOT_FOUND);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("해당 사용자를 찾을 수 없습니다.");

        verify(userRepository, times(1)).findById(userId);
    }

    @DisplayName("비밀번호가 정상적으로 업데이트된다.")
    @Test
    void updatePasswordSuccess() {
        // given
        Long userId = 1L;
        String email = "test@test.com";
        String username = "name";
        String oldPassword = "oldPassword123!";
        String encodedOldPassword = "encodedOldPassword";
        String newPassword = "newPassword123!";
        String encodedNewPassword = "encodedNewPassword";

        AuthUser authUser = new AuthUser(userId, email);
        UserPasswordUpdateRequest dto = new UserPasswordUpdateRequest(oldPassword, newPassword);
        User user = new User(email, username, encodedOldPassword);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(oldPassword, encodedOldPassword)).willReturn(true);
        given(passwordEncoder.encode(newPassword)).willReturn(encodedNewPassword);

        // when
        userService.updatePassword(authUser, dto);

        // then
        assertThat(user.getPassword()).isEqualTo(encodedNewPassword);
        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(oldPassword, encodedOldPassword);
        verify(passwordEncoder, times(1)).encode(newPassword);
    }

    @DisplayName("존재하지 않는 유저 ID로 비밀번호 업데이트 시, USER_NOT_FOUND 예외를 던진다.")
    @Test
    void updatePasswordWithNotFound() {
        // given
        Long userId = -1L;
        String email = "test@test.com";
        String oldPassword = "oldPassword123!";
        String newPassword = "newPassword123!";

        AuthUser authUser = new AuthUser(userId, email);
        UserPasswordUpdateRequest dto = new UserPasswordUpdateRequest(oldPassword, newPassword);

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.updatePassword(authUser, dto),
                "USER_NOT_FOUND expected"
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.USER_NOT_FOUND);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("해당 사용자를 찾을 수 없습니다.");

        verify(userRepository, times(1)).findById(userId);
    }

    @DisplayName("이전 비밀번호가 일치하지 않을 때 INVALID_PASSWORD 예외를 던진다.")
    @Test
    void updatePasswordWithInvalidOldPassword() {
        // given
        Long userId = 1L;
        String email = "test@test.com";
        String username = "name";
        String oldPassword = "wrongPassword123!";
        String encodedOldPassword = "encodedOldPassword";
        String newPassword = "newPassword123!";

        AuthUser authUser = new AuthUser(userId, email);
        UserPasswordUpdateRequest dto = new UserPasswordUpdateRequest(oldPassword, newPassword);
        User user = new User(email, username, encodedOldPassword);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(oldPassword, encodedOldPassword)).willReturn(false);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.updatePassword(authUser, dto),
                "INVALID_PASSWORD expected"
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.INVALID_PASSWORD);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.UNAUTHORIZED);
        assertThat(exception.getMessage()).isEqualTo("입력된 비밀번호가 틀렸습니다.");

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(oldPassword, encodedOldPassword);
    }

    @DisplayName("새 비밀번호가 이전 비밀번호와 동일하면 SAME_AS_OLD_PASSWORD 예외를 던진다.")
    @Test
    void updatePasswordWithSameAsOldPassword() {
        // given
        Long userId = 1L;
        String email = "test@test.com";
        String username = "name";
        String oldPassword = "oldPassword123!";
        String encodedOldPassword = "encodedOldPassword";

        AuthUser authUser = new AuthUser(userId, email);
        UserPasswordUpdateRequest dto = new UserPasswordUpdateRequest(oldPassword, oldPassword);
        User user = new User(email, username, encodedOldPassword);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(oldPassword, encodedOldPassword)).willReturn(true);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.updatePassword(authUser, dto),
                "SAME_AS_OLD_PASSWORD expected"
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.SAME_AS_OLD_PASSWORD);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.CONFLICT);
        assertThat(exception.getMessage()).isEqualTo("기존 비밀번호와 새 비밀번호가 같으면 안 됩니다.");

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(oldPassword, encodedOldPassword);
    }

    @DisplayName("유저가 정상적으로 탈퇴된다.")
    @Test
    void withdrawSuccess() {
        // given
        Long userId = 1L;
        String email = "test@test.com";
        String username = "name";
        String password = "password123!";
        String encodedPassword = "encodedPassword";

        AuthUser authUser = new AuthUser(userId, email);
        UserWithdrawRequest dto = new UserWithdrawRequest(password);
        User user = mock(User.class);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(user.getDeletedAt()).willReturn(null);
        given(user.getPassword()).willReturn(encodedPassword);
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);

        // when
        userService.withdraw(authUser, dto);

        // then
        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verify(user, times(1)).delete();
    }

    @DisplayName("존재하지 않는 유저 ID로 탈퇴 시 USER_NOT_FOUND 예외를 던진다.")
    @Test
    void withdrawWithNotFound() {
        // given
        Long userId = -1L;
        String email = "test@test.com";
        String password = "password123!";

        AuthUser authUser = new AuthUser(userId, email);
        UserWithdrawRequest dto = new UserWithdrawRequest(password);

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.withdraw(authUser, dto),
                "USER_NOT_FOUND expected"
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.USER_NOT_FOUND);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("해당 사용자를 찾을 수 없습니다.");

        verify(userRepository, times(1)).findById(userId);
    }

    @DisplayName("이미 탈퇴한 유저가 탈퇴를 시도하면 ALREADY_DELETED_USER 예외를 던진다.")
    @Test
    void withdrawWithAlreadyDeletedUser() {
        // given
        Long userId = 1L;
        String email = "test@test.com";
        String username = "name";
        String password = "password123!";
        String encodedPassword = "encodedPassword";

        AuthUser authUser = new AuthUser(userId, email);
        UserWithdrawRequest dto = new UserWithdrawRequest(password);

        User user = mock(User.class);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(user.getDeletedAt()).willReturn(LocalDateTime.now());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.withdraw(authUser, dto),
                "ALREADY_DELETED_USER expected"
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.ALREADY_DELETED_USER);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.UNAUTHORIZED);
        assertThat(exception.getMessage()).isEqualTo("이미 탈퇴한 사용자입니다.");

        verify(userRepository, times(1)).findById(userId);
    }

    @DisplayName("비밀번호가 일치하지 않을 때 INVALID_PASSWORD 예외를 던진다.")
    @Test
    void withdrawWithInvalidPassword() {
        // given
        Long userId = 1L;
        String email = "test@test.com";
        String username = "name";
        String password = "wrongPassword123!";
        String encodedPassword = "encodedPassword";

        AuthUser authUser = new AuthUser(userId, email);
        UserWithdrawRequest dto = new UserWithdrawRequest(password);

        User user = mock(User.class);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(user.getDeletedAt()).willReturn(null);
        given(user.getPassword()).willReturn(encodedPassword);
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(false);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.withdraw(authUser, dto),
                "INVALID_PASSWORD expected"
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.INVALID_PASSWORD);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.UNAUTHORIZED);
        assertThat(exception.getMessage()).isEqualTo("입력된 비밀번호가 틀렸습니다.");

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
    }
}
