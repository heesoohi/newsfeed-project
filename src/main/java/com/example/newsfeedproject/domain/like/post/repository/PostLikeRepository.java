package com.example.newsfeedproject.domain.like.post.repository;

import com.example.newsfeedproject.domain.like.post.entity.PostLike;
import com.example.newsfeedproject.domain.post.entity.Post;
import com.example.newsfeedproject.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostAndUser(Post post, User user);
}
