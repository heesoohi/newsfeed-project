package com.example.newsfeedproject.domain.follow.service;

import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.follow.entity.Follow;
import com.example.newsfeedproject.domain.follow.repository.FollowRepository;
import com.example.newsfeedproject.domain.user.entity.User;
import com.example.newsfeedproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void follow(AuthUser authUser, Long targetUserId) {

        // authUser 로 유저 찾아오기
        User fromUser = userRepository.findById(authUser.getUserId()).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );

        // targetUserId 로 유저 찾아오기
        User toUser = userRepository.findById(targetUserId).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );

        followRepository.save(new Follow(fromUser, toUser));

        fromUser.increaseFollowingCount();
        toUser.increaseFollowerCount();
    }

    @Transactional
    public void unfollow(AuthUser authUser, Long targetUserId) {

        User fromUser = userRepository.findById(authUser.getUserId()).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );

        User toUser = userRepository.findById(targetUserId).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );

        Follow follow = followRepository.findByFromUserAndToUser(fromUser, toUser).orElseThrow(
                (() -> new CustomException(ExceptionType.ALREADY_UNFOLLOWED))
        );

        followRepository.delete(follow);

        fromUser.decreaseFollowingCount();
        toUser.decreaseFollowerCount();
    }
}
