package com.example.newsfeedproject.domain.user.controller;

import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.domain.user.dto.response.UserResponse;
import com.example.newsfeedproject.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.BDDMockito.given;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @DisplayName("User 단건 조회 성공")
    @Test
    void getUserSuccess() throws Exception {
        // given
        Long userId = 1L;
        String email = "test@test.com";
        String username = "name";
        int followerCount = 10;
        int followingCount = 5;

        UserResponse userResponse = new UserResponse(userId, email, username, followerCount, followingCount);
        given(userService.getUser(userId)).willReturn(userResponse);

        // when & then
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.followerCount").value(followerCount))
                .andExpect(jsonPath("$.followingCount").value(followingCount));
    }

    @DisplayName("User 단건 조회 실패 - 존재하지 않는 유저")
    @Test
    void getUserNotFound() throws Exception {
        // given
        Long userId = -1L;
        given(userService.getUser(userId)).willThrow(new CustomException(ExceptionType.USER_NOT_FOUND));
        // when & then
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }
}
