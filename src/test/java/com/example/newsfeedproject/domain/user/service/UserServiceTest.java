package com.example.newsfeedproject.domain.user.service;

import com.example.newsfeedproject.common.config.PasswordEncoder;
import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.domain.user.dto.response.UserSaveResponse;
import com.example.newsfeedproject.domain.user.entity.User;
import com.example.newsfeedproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    @DisplayName("중복된 이메일로 유저 저장 시 CustomException 을 던진다.")
    void saveWithDuplicateEmail() {
        // given
        String email = "test@test.com";
        String username = "name";
        String encodedPassword = "encodedPassword";

        given(userRepository.existsByEmail(email)).willReturn(true);

        // when & then
        assertThrows(CustomException.class,
                () -> userService.save(email, username, encodedPassword),
                "DUPLICATE_EMAIL expected"
        );

        verify(userRepository, times(1)).existsByEmail(email);
    }
}
