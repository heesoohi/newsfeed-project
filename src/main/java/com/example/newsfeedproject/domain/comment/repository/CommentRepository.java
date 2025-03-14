package com.example.newsfeedproject.domain.comment.repository;

import com.example.newsfeedproject.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
