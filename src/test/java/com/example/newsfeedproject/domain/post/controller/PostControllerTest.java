package com.example.newsfeedproject.domain.post.controller;

import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.common.pagination.PaginationResponse;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.post.dto.PostRequest;
import com.example.newsfeedproject.domain.post.dto.PostResponse;
import com.example.newsfeedproject.domain.post.dto.PostSaveResponse;
import com.example.newsfeedproject.domain.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org. springframework. data. domain. Pageable;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("게시글 저장 성공")
    @Test
    void savePostSuccess() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        PostRequest dto = new PostRequest("content");
        PostSaveResponse response = new PostSaveResponse(1L);

        given(postService.savePost(any(AuthUser.class), any(PostRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .requestAttr("authUser", authUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(1L));
    }

    @DisplayName("게시글 저장 실패 - 유효하지 않은 요청")
    @Test
    void savePostInvalidRequest() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        PostRequest invalidDto = new PostRequest("");

        // when & then
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .requestAttr("authUser", authUser))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("INVALID_REQUEST"));
    }

    @DisplayName("게시글 저장 실패 - 인증 실패")
    @Test
    void savePostUnauthorized() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        PostRequest dto = new PostRequest("content");

        given(postService.savePost(any(AuthUser.class), any(PostRequest.class)))
                .willThrow(new CustomException(ExceptionType.AUTHENTICATION_FAILED));

        // when & then
        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .requestAttr("authUser", authUser))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유저 인증 정보를 찾을 수 없습니다."));
    }

    @DisplayName("게시글 조회 성공")
    @Test
    void getPostSuccess() throws Exception {
        // given
        Long postId = 1L;
        LocalDateTime createdAt = LocalDateTime.parse("2025-03-19T12:00:00");
        LocalDateTime updatedAt = LocalDateTime.parse("2025-03-19T12:00:00");
        PostResponse response = new PostResponse(postId, "content", "name", createdAt, updatedAt, 10);

        given(postService.getPost(postId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(postId))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.username").value("name"))
                .andExpect(jsonPath("$.createdAt").value("2025-03-19T12:00:00"))
                .andExpect(jsonPath("$.updatedAt").value("2025-03-19T12:00:00"));
    }

    @DisplayName("게시글 조회 실패 - 존재하지 않는 게시글")
    @Test
    void getPostNotFound() throws Exception {
        // given
        Long postId = -1L;

        given(postService.getPost(postId)).willThrow(new CustomException(ExceptionType.POST_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/posts/{postId}", postId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 게시글을 찾을 수 없습니다."));;
    }

    @DisplayName("게시글 목록 조회 성공")
    @Test
    void getAllSuccess() throws Exception {
        // given
        Pageable pageable = (Pageable) PageRequest.of(0, 10, Sort.by("createdAt").descending());
        List<PostResponse> posts = List.of(
                new PostResponse(1L, "content1", "user1", LocalDateTime.parse("2025-03-19T12:00:00"), LocalDateTime.parse("2025-03-19T12:00:00"), 10),
                new PostResponse(2L, "content2", "user2", LocalDateTime.parse("2025-03-19T11:00:00"), LocalDateTime.parse("2025-03-19T11:00:00"), 5)
        );
        Page<PostResponse> page = new PageImpl<>(posts, pageable, 2);
        PaginationResponse<PostResponse> response = new PaginationResponse<>(page);

        given(postService.getAll(any(Pageable.class), eq("createdAt"))).willReturn(response);

        // when & then
        mockMvc.perform(get("/posts")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].postId").value(1L))
                .andExpect(jsonPath("$.data[0].content").value("content1"))
                .andExpect(jsonPath("$.data[0].username").value("user1"))
                .andExpect(jsonPath("$.data[1].postId").value(2L))
                .andExpect(jsonPath("$.paginationInfo.totalElements").value(2))
                .andExpect(jsonPath("$.paginationInfo.totalPages").value(1))
                .andExpect(jsonPath("$.paginationInfo.currentPage").value(0))
                .andExpect(jsonPath("$.paginationInfo.size").value(10));
    }

    @DisplayName("게시글 목록 조회 실패 - 유효하지 않은 정렬 기준")
    @Test
    void getAllInvalidSort() throws Exception {
        given(postService.getAll(any(Pageable.class), eq("invalidSort")))
                .willThrow(new CustomException(ExceptionType.INVALID_REQUEST));

        mockMvc.perform(get("/posts")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "invalidSort"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("요청값 검증에 실패했습니다."));
    }

    @DisplayName("게시글 검색 성공")
    @Test
    void searchPostsSuccess() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        LocalDateTime startDate = LocalDateTime.parse("2025-03-01T00:00:00");
        LocalDateTime endDate = LocalDateTime.parse("2025-03-19T23:59:59");
        List<PostResponse> posts = List.of(
                new PostResponse(1L, "content1", "user1", LocalDateTime.parse("2025-03-19T12:00:00"), LocalDateTime.parse("2025-03-19T12:00:00"), 10)
        );
        Page<PostResponse> page = new PageImpl<>(posts, pageable, 1);
        PaginationResponse<PostResponse> response = new PaginationResponse<>(page);

        given(postService.searchPosts(any(Pageable.class), eq("createdAt"), eq(startDate), eq(endDate)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/posts/search")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt")
                        .param("startDate", "2025-03-01T00:00:00")
                        .param("endDate", "2025-03-19T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].postId").value(1L))
                .andExpect(jsonPath("$.data[0].content").value("content1"))
                .andExpect(jsonPath("$.paginationInfo.totalElements").value(1));
    }

    @DisplayName("게시글 수정 성공")
    @Test
    void updatePostSuccess() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        Long postId = 1L;
        PostRequest dto = new PostRequest("updated content");

        doNothing().when(postService).updatePost(any(AuthUser.class), eq(postId), any(PostRequest.class));

        // when & then
        mockMvc.perform(put("/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .requestAttr("authUser", authUser))
                .andExpect(status().isOk());
    }

    @DisplayName("게시글 수정 실패 - 권한 없음")
    @Test
    void updatePostUnauthorized() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        Long postId = 1L;
        PostRequest dto = new PostRequest("updated content");
        doThrow(new CustomException(ExceptionType.AUTHENTICATION_FAILED)).when(postService).updatePost(any(AuthUser.class), eq(postId), any(PostRequest.class));

        // when & then
        mockMvc.perform(put("/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .requestAttr("authUser", authUser))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유저 인증 정보를 찾을 수 없습니다."));
    }

    @DisplayName("게시글 삭제 성공")
    @Test
    void deletePostSuccess() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        Long postId = 1L;

        doNothing().when(postService).deletePost(any(AuthUser.class), eq(postId));

        // when & then
        mockMvc.perform(delete("/posts/{postId}", postId)
                        .requestAttr("authUser", authUser))
                .andExpect(status().isOk());
    }

    @DisplayName("게시글 삭제 실패 - 존재하지 않는 게시글")
    @Test
    void deletePostNotFound() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        Long postId = -1L;
        doThrow(new CustomException(ExceptionType.POST_NOT_FOUND)).when(postService).deletePost(any(AuthUser.class), eq(postId));

        // when & then
        mockMvc.perform(delete("/posts/{postId}", postId)
                        .requestAttr("authUser", authUser))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 게시글을 찾을 수 없습니다."));
    }

    @DisplayName("팔로잉 게시글 조회 성공")
    @Test
    void getFollowingPostsSuccess() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        Pageable pageable = PageRequest.of(0, 10);
        List<PostResponse> posts = List.of(
                new PostResponse(1L, "content1", "user1", LocalDateTime.parse("2025-03-19T12:00:00"), LocalDateTime.parse("2025-03-19T12:00:00"), 10)
        );
        Page<PostResponse> page = new PageImpl<>(posts, pageable, 1);
        PaginationResponse<PostResponse> response = new PaginationResponse<>(page);

        given(postService.getFollowingPosts(any(AuthUser.class), any(Pageable.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/posts-following")
                        .param("page", "0")
                        .param("size", "10")
                        .requestAttr("authUser", authUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].postId").value(1L))
                .andExpect(jsonPath("$.data[0].content").value("content1"))
                .andExpect(jsonPath("$.paginationInfo.totalElements").value(1));
    }
}
