package com.example.newsfeedproject.domain.follow.repository;

import com.example.newsfeedproject.domain.follow.entity.Follow;
import com.example.newsfeedproject.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFromUserAndToUser(User fromUser, User toUser);
}
