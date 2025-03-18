package com.example.newsfeedproject.domain.post.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.newsfeedproject.domain.follow.entity.Follow;
import com.example.newsfeedproject.domain.post.entity.Post;
import com.example.newsfeedproject.domain.user.entity.User;
import com.example.newsfeedproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("삭제되지 않은 게시글을 페이지로 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        User user1 = new User("test@test.com", "name", "Password123!");
        User user2 = new User("test2@test.com", "name", "Password123!");
        userRepository.saveAll(List.of(user1, user2));

        Post post1 = new Post(user1, "content1");
        Post post2 = new Post(user2, "content2");
        Post deletedPost = new Post(user2, "content3");
        deletedPost.delete();

        postRepository.saveAll(List.of(post1, post2, deletedPost));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Post> posts = postRepository.findAll(pageable);

        // then
        assertNotNull(posts);
        assertEquals(2, posts.getContent().size());
        assertEquals(2, posts.getTotalElements());
        assertTrue(posts.getContent().stream().noneMatch(post -> post.isDeleted()));
        assertEquals(post1.getContent(), posts.getContent().get(0).getContent());
        assertEquals(post2.getContent(), posts.getContent().get(1).getContent());
    }

    @DisplayName("특정 기간 내에 삭제되지 않은 게시글을 페이지로 조회할 수 있다.")
    @Test
    void findByCreatedAtBetween() {
        // given
        User user = new User("test@test.com", "name", "Password123!");
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(2);
        LocalDateTime endDate = now.plusDays(2);

        Post post1 = new Post(user, "content1");
        Post post2 = new Post(user, "content2");
        Post post3 = new Post(user, "content3");
        Post deletedPost = new Post(user, "content4");
        deletedPost.delete();

        entityManager.persist(post1);
        entityManager.persist(post2);
        entityManager.persist(post3);
        entityManager.persist(deletedPost);

        entityManager.getEntityManager()
                .createNativeQuery("UPDATE posts SET created_at = :createdAt WHERE post_id = :postId ")
                .setParameter("createdAt", now.minusDays(5))
                .setParameter("postId", post3.getPostId())
                .executeUpdate();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Post> posts = postRepository.findByCreatedAtBetween(startDate, endDate, pageable);

        // then
        assertNotNull(posts);
        assertEquals(2, posts.getContent().size());
        assertEquals(2, posts.getTotalElements());
        assertTrue(posts.getContent().stream().noneMatch(post -> post.isDeleted()));
        assertTrue(posts.getContent().stream().allMatch(post ->
                !post.getCreatedAt().isBefore(startDate) && !post.getCreatedAt().isAfter(endDate)));
        assertEquals(post1.getContent(), posts.getContent().get(0).getContent());
        assertEquals(post2.getContent(), posts.getContent().get(1).getContent());

    }

    @DisplayName("특정 날짜 이후에 삭제되지 않은 게시글을 페이지로 조회할 수 있다.")
    @Test
    void findByCreatedAtAfter() {
        // given
        User user = new User("test@test.com", "name", "Password123!");
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(2);

        Post post1 = new Post(user, "content1");
        Post post2 = new Post(user, "content2");
        Post post3 = new Post(user, "content3");
        Post deletedPost = new Post(user, "content4");
        deletedPost.delete();

        entityManager.persist(post1);
        entityManager.persist(post2);
        entityManager.persist(post3);
        entityManager.persist(deletedPost);

        entityManager.getEntityManager()
                .createNativeQuery("UPDATE posts SET created_at = :createdAt WHERE post_id = :postId")
                .setParameter("createdAt", now.minusDays(5))
                .setParameter("postId", post3.getPostId())
                .executeUpdate();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Post> posts = postRepository.findByCreatedAtAfter(startDate, pageable);

        // then
        assertNotNull(posts);
        assertEquals(2, posts.getContent().size());
        assertEquals(2, posts.getTotalElements());
        assertTrue(posts.getContent().stream().noneMatch(post -> post.isDeleted()));
        assertTrue(posts.getContent().stream().allMatch(post ->
                !post.getCreatedAt().isBefore(startDate)));
        assertEquals(post1.getContent(), posts.getContent().get(0).getContent());
        assertEquals(post2.getContent(), posts.getContent().get(1).getContent());
    }

    @DisplayName("특정 날짜 이전에 삭제되지 않은 게시글을 페이지로 조회할 수 있다.")
    @Test
    void findByCreatedAtBefore() {
        // given
        User user = new User("test@test.com", "name", "Password123!");
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(2);

        Post post1 = new Post(user, "content1");
        Post post2 = new Post(user, "content2");
        Post post3 = new Post(user, "content3");
        Post deletedPost = new Post(user, "content4");
        deletedPost.delete();

        entityManager.persist(post1);
        entityManager.persist(post2);
        entityManager.persist(post3);
        entityManager.persist(deletedPost);

        entityManager.getEntityManager()
                .createNativeQuery("UPDATE posts SET created_at = :createdAt WHERE post_id = :postId")
                .setParameter("createdAt", now.plusDays(5))
                .setParameter("postId", post3.getPostId())
                .executeUpdate();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Post> posts = postRepository.findByCreatedAtBefore(endDate, pageable);

        // then
        assertNotNull(posts);
        assertEquals(2, posts.getContent().size());
        assertEquals(2, posts.getTotalElements());
        assertTrue(posts.getContent().stream().noneMatch(post -> post.isDeleted()));
        assertTrue(posts.getContent().stream().allMatch(post ->
                !post.getCreatedAt().isAfter(endDate)));
        assertEquals(post1.getContent(), posts.getContent().get(0).getContent());
        assertEquals(post2.getContent(), posts.getContent().get(1).getContent());
    }

    @DisplayName("특정 사용자가 팔로우하는 사용자의 게시글을 페이지로 조회할 수 있다.")
    @Test
    void findAllByFromUser() {
        // given
        User follower = new User("follower@test.com", "follower", "Password123!");
        User following1 = new User("following1@test.com", "following1", "Password123!");
        User following2 = new User("following2@test.com", "following2", "Password123!");
        User user = new User("user@test.com", "user", "Password123!");
        userRepository.saveAll(List.of(follower, following1, following2, user));

        Follow follow1 = new Follow(follower, following1);
        Follow follow2 = new Follow(follower, following2);
        entityManager.persist(follow1);
        entityManager.persist(follow2);

        Post post1 = new Post(following1, "content1");
        Post post2 = new Post(following2, "content2");
        Post post3 = new Post(user, "content3");

        entityManager.persist(post1);
        entityManager.persist(post2);
        entityManager.persist(post3);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Post> posts = postRepository.findAllByFromUser(follower, pageable);

        // then
        assertNotNull(posts);
        assertEquals(2, posts.getContent().size());
        assertEquals(2, posts.getTotalElements());
        assertTrue(posts.getContent().stream().allMatch(post ->
                post.getUser().equals(following1) || post.getUser().equals(following2)));
        assertEquals(post2.getContent(), posts.getContent().get(0).getContent());
        assertEquals(post1.getContent(), posts.getContent().get(1).getContent());

    }
}
