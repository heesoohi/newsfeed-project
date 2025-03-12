package com.example.newsfeedproject.domain.post.repository;

import com.example.newsfeedproject.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL")
    Page<Post> findAll(Pageable pageable);
}
