package com.example.newsfeedproject.domain.like.post.controller;

import com.example.newsfeedproject.common.annotation.Auth;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.like.post.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Void> likePost(
            @Auth AuthUser authUser,
            @PathVariable("postId") Long postId
    ) {
        postLikeService.likePost(authUser, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            @Auth AuthUser authUser,
            @PathVariable("postId") Long postId
    ) {
        postLikeService.unlikePost(authUser, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
