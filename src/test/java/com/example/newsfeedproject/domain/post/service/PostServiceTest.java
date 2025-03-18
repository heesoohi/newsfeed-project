package com.example.newsfeedproject.domain.post.service;

import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.post.dto.PostRequest;
import com.example.newsfeedproject.domain.post.dto.PostResponse;
import com.example.newsfeedproject.domain.post.dto.PostSaveResponse;
import com.example.newsfeedproject.domain.post.entity.Post;
import com.example.newsfeedproject.domain.post.repository.PostRepository;
import com.example.newsfeedproject.domain.user.entity.User;
import com.example.newsfeedproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    @DisplayName("게시글을 성공적으로 저장한다.")
    @Test
    void savePostSuccess() {
        // given
        Long userId = 1L;
        AuthUser authUser = new AuthUser(userId, "test@test.com");
        String content = "test content";
        PostRequest postRequest = new PostRequest(content);

        User user = new User("test@test.com", "name","Password123!");
        ReflectionTestUtils.setField(user, "userId", userId);

        Post post = new Post(user, content);
        Post savedPost = new Post(user, content);
        Long postId = 1L;
        ReflectionTestUtils.setField(savedPost, "postId", postId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class))).willReturn(savedPost);

        // when
        PostSaveResponse response = postService.savePost(authUser, postRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPostId()).isEqualTo(postId);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @DisplayName("존재하지 않는 유저로 게시글 저장 시 USER_NOT_FOUND 예외를 던진다.")
    @Test
    void savePostNotFoundUser() {
        // given
        Long userId = 1L;
        AuthUser authUser = new AuthUser(userId, "test@test.com");
        String content = "test content";
        PostRequest postRequest = new PostRequest(content);

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class,
                () -> postService.savePost(authUser, postRequest)
        );
        verify(postRepository, never()).save(any(Post.class));
    }

    @DisplayName("게시글을 성공적으로 조회한다.")
    @Test
    void getPostSuccess() {
        // given
        Long postId = 1L;
        User user = new User("test@test.com", "name","Password123!");
        ReflectionTestUtils.setField(user, "userId", 1L);

        String content = "test content";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        Post post = new Post(user, content);
        ReflectionTestUtils.setField(post, "postId", postId);
        ReflectionTestUtils.setField(post, "createdAt", createdAt);
        ReflectionTestUtils.setField(post, "updatedAt", updatedAt);
        ReflectionTestUtils.setField(post, "likeCount", 10);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        PostResponse response = postService.getPost(postId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPostId()).isEqualTo(postId);
        assertThat(response.getContent()).isEqualTo(content);
        assertThat(response.getUsername()).isEqualTo("name");
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
        assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(response.getLikeCount()).isEqualTo(10);
    }

    @DisplayName("존재하지 않는 게시글 조회 시 POST_NOT_FOUND 예외를 던진다.")
    @Test
    void getPostNotFound() {
        // given
        Long postId = 1L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> postService.getPost(postId)
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.POST_NOT_FOUND);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("해당 게시글을 찾을 수 없습니다.");

        verify(postRepository, times(1)).findById(postId);    }
}
