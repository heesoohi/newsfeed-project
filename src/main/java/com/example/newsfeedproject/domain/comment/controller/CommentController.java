package com.example.newsfeedproject.domain.comment.controller;

import com.example.newsfeedproject.common.annotation.Auth;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.comment.dto.request.CommentRequest;
import com.example.newsfeedproject.domain.comment.dto.response.CommentSaveResponse;
import com.example.newsfeedproject.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentSaveResponse> saveComment(
            @Auth AuthUser authUser,
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest dto
            ) {
        return ResponseEntity.ok(commentService.saveComment(authUser, postId, dto));
    }
}
