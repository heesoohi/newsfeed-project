package com.example.newsfeedproject.domain.follow.controller;

import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.follow.service.FollowService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(FollowController.class)
public class FollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FollowService followService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("팔로우 성공")
    @Test
    void followSuccess() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        Long targetUserId = 2L;
        doNothing().when(followService).follow(any(AuthUser.class), any(Long.class));

        // when & then
        mockMvc.perform(post("/follow/{targetUserId}", targetUserId)
                        .requestAttr("authUser", authUser))
                .andExpect(status().isOk());
    }

    @DisplayName("언팔로우 성공 - 올바른 사용자 정보로 언팔로우 요청 시 200 OK 반환")
    @Test
    void unfollowSuccess() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        Long targetUserId = 2L;
        doNothing().when(followService).unfollow(any(AuthUser.class), any(Long.class));

        // when & then
        mockMvc.perform(delete("/follow/{targetUserId}", targetUserId)
                        .requestAttr("authUser", authUser))
                .andExpect(status().isOk());

        // verify
        verify(followService).unfollow(any(AuthUser.class), eq(targetUserId));
    }
}
