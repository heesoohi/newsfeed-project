package com.example.newsfeedproject.domain.post.repository;

import com.example.newsfeedproject.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
