package com.example.newsfeedproject.domain.post.repository;

import com.example.newsfeedproject.domain.post.entity.Post;
import com.example.newsfeedproject.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL")
    Page<Post> findAll(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND p.createdAt BETWEEN :startDate And :endDate")
    Page<Post> findByCreatedAtBetween(
            @Param("startDate")LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND p.createdAt >= :startDate")
    Page<Post> findByCreatedAtAfter(
            @Param("startDate")LocalDateTime startDate,
            Pageable pageable
            );

    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND p.createdAt <= :endDate")
    Page<Post> findByCreatedAtBefore(
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query(
            value = "SELECT p FROM Post p " +
                    "JOIN p.user u " +
                    "JOIN Follow f ON u = f.toUser " +
                    "WHERE f.fromUser = :user " +
                    "ORDER BY p.createdAt DESC",
            countQuery = "SELECT COUNT(p) FROM Post p " +
                    "JOIN p.user u " +
                    "JOIN Follow f ON u = f.toUser " +
                    "WHERE f.fromUser = :user"
    )
    Page<Post> findAllByFromUser(@Param("user") User fromUser, Pageable pageable);
}
