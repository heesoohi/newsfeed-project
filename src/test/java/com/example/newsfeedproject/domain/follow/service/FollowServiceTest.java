package com.example.newsfeedproject.domain.follow.service;

import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.follow.entity.Follow;
import com.example.newsfeedproject.domain.follow.repository.FollowRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FollowService followService;

    @DisplayName("유저가 다른 유저를 정상적으로 팔로우한다.")
    @Test
    void followSuccess() {
        // given
        Long fromUserId = 1L;
        Long toUserId = 2L;
        AuthUser authUser = new AuthUser(fromUserId, "test1@test.com");

        User fromUser = mock(User.class);
        User toUser = mock(User.class);

        given(userRepository.findById(fromUserId)).willReturn(Optional.of(fromUser));
        given(userRepository.findById(toUserId)).willReturn(Optional.of(toUser));
        given(followRepository.save(any(Follow.class))).willReturn(new Follow(fromUser, toUser));

        // when
        followService.follow(authUser, toUserId);

        // then
        verify(userRepository, times(1)).findById(fromUserId);
        verify(userRepository, times(1)).findById(toUserId);
        verify(followRepository, times(1)).save(any(Follow.class));
        verify(fromUser, times(1)).increaseFollowingCount();
        verify(toUser, times(1)).increaseFollowerCount();

    }

    @DisplayName("저장되지 않은 유저가 팔로우 요청시 USER_NOT_FOUND 예외를 던진다.")
    @Test
    void followWithNotFound() {
        // given
        Long fromUserId = -1L;
        Long toUserId = 2L;
        AuthUser authUser = new AuthUser(fromUserId, "test1@test.com");

        User fromUser = mock(User.class);
        User toUser = mock(User.class);

        given(userRepository.findById(fromUserId)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> followService.follow(authUser, toUserId),
                "User not found");
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.USER_NOT_FOUND);
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("해당 사용자를 찾을 수 없습니다.");

        verify(userRepository, times(1)).findById(fromUserId);
    }

    @DisplayName("유저가 다른 유저를 정상적으로 언팔로우한다.")
    @Test
    void unfollowSuccess() {
        // given
        Long fromUserId = 1L;
        Long toUserId = 2L;
        AuthUser authUser = new AuthUser(fromUserId, "test1@test.com");

        User fromUser = mock(User.class);
        User toUser = mock(User.class);
        Follow follow = mock(Follow.class);

        given(userRepository.findById(fromUserId)).willReturn(Optional.of(fromUser));
        given(userRepository.findById(toUserId)).willReturn(Optional.of(toUser));
        given(followRepository.findByFromUserAndToUser(fromUser, toUser)).willReturn(Optional.of(follow));

        // when
        followService.unfollow(authUser, toUserId);

        // then
        verify(userRepository, times(1)).findById(fromUserId);
        verify(userRepository, times(1)).findById(toUserId);
        verify(followRepository, times(1)).findByFromUserAndToUser(fromUser, toUser);
        verify(fromUser, times(1)).decreaseFollowingCount();
        verify(toUser, times(1)).decreaseFollowerCount();

    }
}
