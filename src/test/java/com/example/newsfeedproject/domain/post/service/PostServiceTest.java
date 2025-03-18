package com.example.newsfeedproject.domain.post.service;

import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.common.pagination.PaginationResponse;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.post.dto.PostRequest;
import com.example.newsfeedproject.domain.post.dto.PostResponse;
import com.example.newsfeedproject.domain.post.dto.PostSaveResponse;
import com.example.newsfeedproject.domain.post.entity.Post;
import com.example.newsfeedproject.domain.post.repository.PostRepository;
import com.example.newsfeedproject.domain.user.entity.User;
import com.example.newsfeedproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    private User user;
    private Post post1;
    private Post post2;
    private Post post3;
    private List<Post> posts;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // 공통 given 설정
        user = new User("test@test.com", "name", "Password123!");
        ReflectionTestUtils.setField(user, "userId", 1L);

        post1 = new Post(user, "content 1");
        ReflectionTestUtils.setField(post1, "postId", 1L);
        ReflectionTestUtils.setField(post1, "createdAt", LocalDateTime.now().minusDays(2));
        ReflectionTestUtils.setField(post1, "updatedAt", LocalDateTime.now().minusDays(2));
        ReflectionTestUtils.setField(post1, "likeCount", 5);

        post2 = new Post(user, "content 2");
        ReflectionTestUtils.setField(post2, "postId", 2L);
        ReflectionTestUtils.setField(post2, "createdAt", LocalDateTime.now().minusDays(1));
        ReflectionTestUtils.setField(post2, "updatedAt", LocalDateTime.now().minusDays(1));
        ReflectionTestUtils.setField(post2, "likeCount", 10);

        post3 = new Post(user, "content 3");
        ReflectionTestUtils.setField(post3, "postId", 3L);
        ReflectionTestUtils.setField(post3, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(post3, "updatedAt", LocalDateTime.now());
        ReflectionTestUtils.setField(post3, "likeCount", 15);

        posts = List.of(post3, post2, post1); // 기본적으로 createdAt 내림차순
        pageable = PageRequest.of(0, 10);
    }

    @DisplayName("게시글을 성공적으로 저장한다.")
    @Test
    void savePostSuccess() {
        // given
        Long userId = 1L;
        AuthUser authUser = new AuthUser(userId, "test@test.com");
        String content = "test content";
        PostRequest postRequest = new PostRequest(content);

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
        CustomException exception = assertThrows(CustomException.class,
                () -> postService.savePost(authUser, postRequest)
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.USER_NOT_FOUND);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("해당 사용자를 찾을 수 없습니다.");

        verify(postRepository, never()).save(any(Post.class));
    }

    @DisplayName("게시글을 성공적으로 조회한다.")
    @Test
    void getPostSuccess() {
        // given
        Long postId = 1L;
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

        verify(postRepository, times(1)).findById(postId);
    }

    @DisplayName("모든 게시글을 생성일 기준으로 페이지네이션하여 성공적으로 조회한다.")
    @Test
    void getAllPostsByCreatedAtSuccess() {
        // given
        String sort = null; //기본값이 createdAt
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());
        given(postRepository.findAll(any(Pageable.class))).willReturn(postPage);

        // when
        PaginationResponse<PostResponse> response = postService.getAll(pageable, sort);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getData()).hasSize(3);
        List<PostResponse> data = response.getData();
        assertThat(data.get(0)).extracting(PostResponse::getPostId).isEqualTo(3L);
        assertThat(data.get(1)).extracting(PostResponse::getPostId).isEqualTo(2L);
        assertThat(data.get(2)).extracting(PostResponse::getPostId).isEqualTo(1L);
        assertThat(data.get(0)).extracting(PostResponse::getContent).isEqualTo("content 3");
        assertThat(data.get(1)).extracting(PostResponse::getContent).isEqualTo("content 2");
        assertThat(data.get(2)).extracting(PostResponse::getContent).isEqualTo("content 1");

        assertThat(response.getPaginationInfo().getCurrentPage()).isEqualTo(0);
        assertThat(response.getPaginationInfo().getTotalPages()).isEqualTo(1);
        assertThat(response.getPaginationInfo().getTotalElements()).isEqualTo(3L);
        assertThat(response.getPaginationInfo().getSize()).isEqualTo(10);
        verify(postRepository, times(1)).findAll(any(Pageable.class));
    }

    @DisplayName("모든 게시글을 좋아요 수 기준으로 페이지네이션 하여 성공적으로 조회한다.")
    @Test
    void getAllPostsByLikeCountSuccess() {
        // given
        String sort = "likeCount";
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());
        given(postRepository.findAll(any(Pageable.class))).willReturn(postPage);

        // when
        PaginationResponse<PostResponse> response = postService.getAll(pageable, sort);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getData()).hasSize(3);
        List<PostResponse> data = response.getData();

        assertThat(data.get(0)).extracting(PostResponse::getPostId).isEqualTo(3L);
        assertThat(data.get(1)).extracting(PostResponse::getPostId).isEqualTo(2L);
        assertThat(data.get(2)).extracting(PostResponse::getPostId).isEqualTo(1L);
        assertThat(data.get(0)).extracting(PostResponse::getLikeCount).isEqualTo(15);
        assertThat(data.get(1)).extracting(PostResponse::getLikeCount).isEqualTo(10);
        assertThat(data.get(2)).extracting(PostResponse::getLikeCount).isEqualTo(5);
        assertThat(response.getPaginationInfo().getCurrentPage()).isEqualTo(0);
        assertThat(response.getPaginationInfo().getTotalPages()).isEqualTo(1);
        assertThat(response.getPaginationInfo().getTotalElements()).isEqualTo(3L);
        assertThat(response.getPaginationInfo().getSize()).isEqualTo(10);
        verify(postRepository, times(1)).findAll(any(Pageable.class));
    }

    @DisplayName("특정 날짜 범위 내 게시글을 생성일 기준으로 검색하여 성공적으로 조회.")
    @Test
    void searchPostsByCreatedAtSuccess() {
        // given
        String sort = null; // 기본갑: createdAt
        LocalDateTime startDate = LocalDateTime.now().minusDays(3);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        Pageable searchPageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));

        List<Post> filteredPosts = List.of(post3, post2, post1);
        Page<Post> postPage = new PageImpl<>(filteredPosts, searchPageable, filteredPosts.size());
        given(postRepository.findByCreatedAtBetween(eq(startDate), eq(endDate), any(Pageable.class))).willReturn(postPage);

        // when
        PaginationResponse<PostResponse> response = postService.searchPosts(pageable, sort, startDate, endDate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getData()).hasSize(3);
        List<PostResponse> data = response.getData();
        assertThat(data.get(0)).extracting(PostResponse::getPostId).isEqualTo(3L);
        assertThat(data.get(1)).extracting(PostResponse::getPostId).isEqualTo(2L);
        assertThat(data.get(2)).extracting(PostResponse::getPostId).isEqualTo(1L);
        assertThat(data.get(0)).extracting(PostResponse::getContent).isEqualTo("content 3");
        assertThat(data.get(1)).extracting(PostResponse::getContent).isEqualTo("content 2");
        assertThat(data.get(2)).extracting(PostResponse::getContent).isEqualTo("content 1");

        assertThat(response.getPaginationInfo().getCurrentPage()).isEqualTo(0);
        assertThat(response.getPaginationInfo().getTotalPages()).isEqualTo(1);
        assertThat(response.getPaginationInfo().getTotalElements()).isEqualTo(3L);
        assertThat(response.getPaginationInfo().getSize()).isEqualTo(10);
        verify(postRepository, times(1)).findByCreatedAtBetween(eq(startDate), eq(endDate), any(Pageable.class));
    }

    @DisplayName("특정 날짜 범위 내 게시글을 업데이트 날짜 기준으로 검색하여 성공적으로 조회.")
    @Test
    void searchPostsByUpdatedAtSuccess() {
        // given
        String sort = "updatedAt";
        LocalDateTime startDate = LocalDateTime.now().minusDays(3);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        Pageable searchPageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("updatedAt")));

        List<Post> filteredPosts = List.of(post3, post2, post1);
        Page<Post> postPage = new PageImpl<>(filteredPosts, searchPageable, filteredPosts.size());
        given(postRepository.findByCreatedAtBetween(eq(startDate), eq(endDate), any(Pageable.class))).willReturn(postPage);

        // when
        PaginationResponse<PostResponse> response = postService.searchPosts(pageable, sort, startDate, endDate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getData()).hasSize(3);
        List<PostResponse> data = response.getData();
        assertThat(data.get(0)).extracting(PostResponse::getPostId).isEqualTo(3L);
        assertThat(data.get(1)).extracting(PostResponse::getPostId).isEqualTo(2L);
        assertThat(data.get(2)).extracting(PostResponse::getPostId).isEqualTo(1L);
        assertThat(data.get(0)).extracting(PostResponse::getUpdatedAt).isEqualTo(post3.getUpdatedAt());
        assertThat(data.get(1)).extracting(PostResponse::getUpdatedAt).isEqualTo(post2.getUpdatedAt());
        assertThat(data.get(2)).extracting(PostResponse::getUpdatedAt).isEqualTo(post1.getUpdatedAt());

        assertThat(response.getPaginationInfo().getCurrentPage()).isEqualTo(0);
        assertThat(response.getPaginationInfo().getTotalPages()).isEqualTo(1);
        assertThat(response.getPaginationInfo().getTotalElements()).isEqualTo(3L);
        assertThat(response.getPaginationInfo().getSize()).isEqualTo(10);

        verify(postRepository, times(1)).findByCreatedAtBetween(eq(startDate), eq(endDate), any(Pageable.class));
    }

    @DisplayName("시작 날짜만 제공된 경우 게시글을 성공적으로 검색한다.")
    @Test
    void searchPostsWithStartDateOnlySuccess() {
        // given
        String sort = null;
        LocalDateTime startDate = LocalDateTime.now().minusDays(3);
        LocalDateTime endDate = null;
        Pageable searchPageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));

        List<Post> filteredPosts = List.of(post3, post2, post1);
        Page<Post> postPage = new PageImpl<>(filteredPosts, searchPageable, filteredPosts.size());
        given(postRepository.findByCreatedAtAfter(eq(startDate), any(Pageable.class))).willReturn(postPage);

        // when
        PaginationResponse<PostResponse> response = postService.searchPosts(pageable, sort, startDate, endDate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getData()).hasSize(3);
        List<PostResponse> data = response.getData();
        assertThat(data.get(0)).extracting(PostResponse::getPostId).isEqualTo(3L);
        assertThat(data.get(1)).extracting(PostResponse::getPostId).isEqualTo(2L);
        assertThat(data.get(2)).extracting(PostResponse::getPostId).isEqualTo(1L);

        assertThat(response.getPaginationInfo().getCurrentPage()).isEqualTo(0);
        assertThat(response.getPaginationInfo().getTotalPages()).isEqualTo(1);
        assertThat(response.getPaginationInfo().getTotalElements()).isEqualTo(3L);
        assertThat(response.getPaginationInfo().getSize()).isEqualTo(10);
        verify(postRepository, times(1)).findByCreatedAtAfter(eq(startDate), any(Pageable.class));

    }

    @DisplayName("종료 날짜만 제공된 경우 게시글을 성공적으로 검색한다.")
    @Test
    void searchPostsWithEndDateOnlySuccess() {
        // given
        String sort = null; // 기본값: createdAt
        LocalDateTime startDate = null;
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        Pageable searchPageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));

        List<Post> filteredPosts = List.of(post3, post2, post1); // endDate 이전 게시글
        Page<Post> postPage = new PageImpl<>(filteredPosts, searchPageable, filteredPosts.size());
        given(postRepository.findByCreatedAtBefore(eq(endDate), any(Pageable.class)))
                .willReturn(postPage);

        // when
        PaginationResponse<PostResponse> response = postService.searchPosts(pageable, sort, startDate, endDate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getData()).hasSize(3);
        List<PostResponse> data = response.getData();
        assertThat(data.get(0)).extracting(PostResponse::getPostId).isEqualTo(3L);
        assertThat(data.get(1)).extracting(PostResponse::getPostId).isEqualTo(2L);
        assertThat(data.get(2)).extracting(PostResponse::getPostId).isEqualTo(1L);

        assertThat(response.getPaginationInfo().getCurrentPage()).isEqualTo(0);
        assertThat(response.getPaginationInfo().getTotalPages()).isEqualTo(1);
        assertThat(response.getPaginationInfo().getTotalElements()).isEqualTo(3L);
        assertThat(response.getPaginationInfo().getSize()).isEqualTo(10);
        verify(postRepository, times(1)).findByCreatedAtBefore(eq(endDate), any(Pageable.class));
    }

    @DisplayName("날짜 범위 없이 모든 게시글을 검색하여 성공적으로 조회한다.")
    @Test
    void searchPostsWithoutDateRangeSuccess() {
        // given
        String sort = null; // 기본값: createdAt
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        Pageable searchPageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));

        List<Post> filteredPosts = List.of(post3, post2, post1); // 모든 게시글
        Page<Post> postPage = new PageImpl<>(filteredPosts, searchPageable, filteredPosts.size());
        given(postRepository.findAll(any(Pageable.class))).willReturn(postPage);

        // when
        PaginationResponse<PostResponse> response = postService.searchPosts(pageable, sort, startDate, endDate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getData()).hasSize(3);
        List<PostResponse> data = response.getData();
        assertThat(data.get(0)).extracting(PostResponse::getPostId).isEqualTo(3L);
        assertThat(data.get(1)).extracting(PostResponse::getPostId).isEqualTo(2L);
        assertThat(data.get(2)).extracting(PostResponse::getPostId).isEqualTo(1L);

        assertThat(response.getPaginationInfo().getCurrentPage()).isEqualTo(0);
        assertThat(response.getPaginationInfo().getTotalPages()).isEqualTo(1);
        assertThat(response.getPaginationInfo().getTotalElements()).isEqualTo(3L);
        assertThat(response.getPaginationInfo().getSize()).isEqualTo(10);
        verify(postRepository, times(1)).findAll(any(Pageable.class));
    }

    @DisplayName("게시글을 성공적으로 수정한다.")
    @Test
    void updatePostSuccess() {
        // given
        Long userId = 1L;
        Long postId = 1L;
        AuthUser authUser = new AuthUser(userId, "test@test.com");
        String newContent = "updated content";
        PostRequest postRequest = new PostRequest(newContent);

        given(postRepository.findById(postId)).willReturn(Optional.of(post1));

        // when
        postService.updatePost(authUser, postId, postRequest);
        
        // then
        assertThat(post1.getContent()).isEqualTo(newContent);
        verify(postRepository, times(1)).findById(postId);
    }

    @DisplayName("존재하지 않는 게시글 수정 시 POST_NOT_FOUND 예외를 던진다.")
    @Test
    void updatePostNotFound() {
        // given
        Long userId = 1L;
        Long postId = -1L; // 존재하지 않는 게시글 ID
        AuthUser authUser = new AuthUser(userId, "test@test.com");
        String newContent = "updated content";
        PostRequest postRequest = new PostRequest(newContent);

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> postService.updatePost(authUser, postId, postRequest)
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.POST_NOT_FOUND);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("해당 게시글을 찾을 수 없습니다.");

        verify(postRepository, times(1)).findById(postId);
    }

    @DisplayName("권한 없는 사용자가 게시글 수정 시 NO_PERMISSION_ACTION 예외를 던진다.")
    @Test
    void updatePostNoPermission() {
        // given
        Long userId = 1L;
        Long otherUserId = 2L;
        Long postId = 1L;
        AuthUser authUser = new AuthUser(otherUserId, "other@test.com"); // 다른 사용자
        String newContent = "updated content";
        PostRequest postRequest = new PostRequest(newContent);

        given(postRepository.findById(postId)).willReturn(Optional.of(post1));

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> postService.updatePost(authUser, postId, postRequest)
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.NO_PERMISSION_ACTION);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.FORBIDDEN);
        assertThat(exception.getMessage()).isEqualTo("권한이 없는 작업입니다.");

        verify(postRepository, times(1)).findById(postId);
    }

    @DisplayName("게시글을 성공적으로 삭제한다.")
    @Test
    void deletePostSuccess() {
        // given
        Long userId = 1L;
        Long postId = 1L;
        AuthUser authUser = new AuthUser(userId, "test@test.com");

        given(postRepository.findById(postId)).willReturn(Optional.of(post1));

        // when
        postService.deletePost(authUser, postId);

        // then
        verify(postRepository, times(1)).findById(postId);
        assertThat(post1.isDeleted()).isTrue();
        assertThat(post1.getDeletedAt()).isNotNull();
    }

    @DisplayName("존재하지 않는 게시글 삭제 시 POST_NOT_FOUND 예외를 던진다.")
    @Test
    void deletePostNotFound() {
        // given
        Long userId = 1L;
        Long postId = -1L; // 존재하지 않는 게시글 ID
        AuthUser authUser = new AuthUser(userId, "test@test.com");

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> postService.deletePost(authUser, postId)
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.POST_NOT_FOUND);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("해당 게시글을 찾을 수 없습니다.");

        verify(postRepository, times(1)).findById(postId);
    }

    @DisplayName("권한 없는 사용자가 게시글 삭제 시 NO_PERMISSION_ACTION 예외를 던진다.")
    @Test
    void deletePostNoPermission() {
        // given
        Long userId = 1L;
        Long otherUserId = 2L;
        Long postId = 1L;
        AuthUser authUser = new AuthUser(otherUserId, "other@test.com"); // 다른 사용자

        given(postRepository.findById(postId)).willReturn(Optional.of(post1));

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> postService.deletePost(authUser, postId)
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.NO_PERMISSION_ACTION);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.FORBIDDEN);
        assertThat(exception.getMessage()).isEqualTo("권한이 없는 작업입니다.");

        verify(postRepository, times(1)).findById(postId);
    }

    @DisplayName("팔로잉 게시글을 성공적으로 조회한다.")
    @Test
    void getFollowingPostsSuccess() {
        // given
        Long userId = 1L;
        AuthUser authUser = new AuthUser(userId, "test@test.com");
        Pageable followingPageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));
        Page<Post> postPage = new PageImpl<>(posts, followingPageable, posts.size());

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(postRepository.findAllByFromUser(eq(user), any(Pageable.class))).willReturn(postPage);

        // when
        PaginationResponse<PostResponse> response = postService.getFollowingPosts(authUser, pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getData()).hasSize(3);
        List<PostResponse> data = response.getData();
        assertThat(data.get(0)).extracting(PostResponse::getPostId).isEqualTo(3L);
        assertThat(data.get(1)).extracting(PostResponse::getPostId).isEqualTo(2L);
        assertThat(data.get(2)).extracting(PostResponse::getPostId).isEqualTo(1L);
        assertThat(data.get(0)).extracting(PostResponse::getContent).isEqualTo("content 3");
        assertThat(data.get(1)).extracting(PostResponse::getContent).isEqualTo("content 2");
        assertThat(data.get(2)).extracting(PostResponse::getContent).isEqualTo("content 1");

        assertThat(response.getPaginationInfo().getCurrentPage()).isEqualTo(0);
        assertThat(response.getPaginationInfo().getTotalPages()).isEqualTo(1);
        assertThat(response.getPaginationInfo().getTotalElements()).isEqualTo(3L);
        assertThat(response.getPaginationInfo().getSize()).isEqualTo(10);

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).findAllByFromUser(eq(user), any(Pageable.class));
    }

    @DisplayName("인증되지 않은 사용자로 팔로잉 게시글 조회 시 AUTHENTICATION_FAILED 예외를 던진다.")
    @Test
    void getFollowingPostsAuthenticationFailed() {
        // given
        AuthUser authUser = null;

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> postService.getFollowingPosts(authUser, pageable)
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.AUTHENTICATION_FAILED);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.UNAUTHORIZED);
        assertThat(exception.getMessage()).isEqualTo("유저 인증 정보를 찾을 수 없습니다.");

        verify(userRepository, never()).findById(any());
        verify(postRepository, never()).findAllByFromUser(any(), any(Pageable.class));
    }

    @DisplayName("존재하지 않는 사용자로 팔로잉 게시글 조회 시 USER_NOT_FOUND 예외를 던진다.")
    @Test
    void getFollowingPostsUserNotFound() {
        // given
        Long userId = 1L;
        AuthUser authUser = new AuthUser(userId, "test@test.com");

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> postService.getFollowingPosts(authUser, pageable)
        );
        assertThat(exception.getExceptionType()).isEqualTo(ExceptionType.USER_NOT_FOUND);
        assertThat(exception.getHttpStatus()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("해당 사용자를 찾을 수 없습니다.");

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, never()).findAllByFromUser(any(), any(Pageable.class));
    }
}
