package com.example.newsfeedproject.domain.follow.service;

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
}
