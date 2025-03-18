package com.example.newsfeedproject.domain.user.controller;

import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.user.dto.request.UserPasswordUpdateRequest;
import com.example.newsfeedproject.domain.user.dto.request.UserUpdateRequest;
import com.example.newsfeedproject.domain.user.dto.request.UserWithdrawRequest;
import com.example.newsfeedproject.domain.user.dto.response.UserResponse;
import com.example.newsfeedproject.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

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
        // API 호출은 mockMvc 사용.
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

    @DisplayName("본인 프로필 수정 성공")
    @Test
    void updateUserSuccess() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        UserUpdateRequest dto = new UserUpdateRequest("newName");
        doNothing().when(userService).updateUser(any(AuthUser.class), any(UserUpdateRequest.class));

        // when & then
        mockMvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .requestAttr("authUser", authUser)) // @Auth 로 주입되는 객체를 MockMvc 에 설정
                .andExpect(status().isOk());
    }

    @DisplayName("본인 프로필 수정 실패 - 유효하지 않은 요청")
    @Test
    void updateUserInvalidRequest() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        UserUpdateRequest invalidDto = new UserUpdateRequest("");

        // when & then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .requestAttr("authUser", authUser))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("본인 프로필 수정 실패 - 권한 없음")
    @Test
    void updateUserUnauthorized() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        UserUpdateRequest dto = new UserUpdateRequest("newName");
        doThrow(new CustomException(ExceptionType.AUTHENTICATION_FAILED)).when(userService).updateUser(any(AuthUser.class), any(UserUpdateRequest.class));

        // when & then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .requestAttr("authUser", authUser))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("비밀번호 수정 성공")
    @Test
    void updatePasswordSuccess() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        UserPasswordUpdateRequest dto = new UserPasswordUpdateRequest("oldPass123!", "newPass123!");
        doNothing().when(userService).updatePassword(any(AuthUser.class), any(UserPasswordUpdateRequest.class));

        // when & then
        mockMvc.perform(put("/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .requestAttr("authUser", authUser))
                .andExpect(status().isOk());
    }

    @DisplayName("비밀번호 수정 실패 - 유효하지 않은 요청")
    @Test
    void updatePasswordInvalidRequest() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        UserPasswordUpdateRequest invalidDto = new UserPasswordUpdateRequest("oldPass123!", "");

        // when & then
        mockMvc.perform(put("/users/password")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto))
                .requestAttr("authUser", authUser))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("비밀번호 수정 실패 - 권한 없음")
    @Test
    void updatePasswordUnauthorized() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        UserPasswordUpdateRequest dto = new UserPasswordUpdateRequest("oldPass123!", "newPass123!");
        doThrow(new CustomException(ExceptionType.AUTHENTICATION_FAILED)).when(userService).updatePassword(any(AuthUser.class), any(UserPasswordUpdateRequest.class));

        // when & then
        mockMvc.perform(put("/users/password")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .requestAttr("authUser", authUser))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("회원 탈퇴 성공")
    @Test
    void withdrawUserSuccess() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        UserWithdrawRequest dto = new UserWithdrawRequest("Password123!");
        doNothing().when(userService).withdraw(any(AuthUser.class), any(UserWithdrawRequest.class));

        // when & then
        mockMvc.perform(post("/users/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .requestAttr("authUser", authUser))
                .andExpect(status().isOk());
    }

    @DisplayName("회원 탈퇴 실패 - 유효하지 않은 요청")
    @Test
    void withdrawUserInvalidRequest() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        UserWithdrawRequest invalidDto = new UserWithdrawRequest("");

        // when & then
        mockMvc.perform(post("/users/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .requestAttr("authUser", authUser))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("회원 탈퇴 실패 - 권한 없음")
    @Test
    void withdrawUserUnauthorized() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com");
        UserWithdrawRequest dto = new UserWithdrawRequest("Password123!");
        doThrow(new CustomException(ExceptionType.AUTHENTICATION_FAILED))
                .when(userService).withdraw(any(AuthUser.class), any(UserWithdrawRequest.class));

        // when & then
        mockMvc.perform(post("/users/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .requestAttr("authUser", authUser))
                .andExpect(status().isUnauthorized());
    }
}
