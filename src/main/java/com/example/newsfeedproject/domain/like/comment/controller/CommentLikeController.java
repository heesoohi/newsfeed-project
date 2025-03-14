package com.example.newsfeedproject.domain.like.comment.controller;

import com.example.newsfeedproject.common.annotation.Auth;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.like.comment.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping("/posts/{postId}/comments/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @Auth AuthUser authUser,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentLikeService.likeComment(authUser, postId, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
