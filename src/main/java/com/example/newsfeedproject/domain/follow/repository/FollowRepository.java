package com.example.newsfeedproject.domain.follow.repository;

import com.example.newsfeedproject.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
}
