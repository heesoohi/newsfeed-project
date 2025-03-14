package com.example.newsfeedproject.domain.like.comment.repository;

import com.example.newsfeedproject.domain.comment.entity.Comment;
import com.example.newsfeedproject.domain.like.comment.entity.CommentLike;
import com.example.newsfeedproject.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
}
