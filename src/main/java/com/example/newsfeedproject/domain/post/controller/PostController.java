package com.example.newsfeedproject.domain.post.controller;

import com.example.newsfeedproject.common.annotation.Auth;
import com.example.newsfeedproject.common.pagination.PaginationResponse;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.post.dto.PostResponse;
import com.example.newsfeedproject.domain.post.dto.PostRequest;
import com.example.newsfeedproject.domain.post.dto.PostSaveResponse;
import com.example.newsfeedproject.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<PostSaveResponse> savePost(
            @Auth AuthUser authUser,
            @Valid @RequestBody PostRequest dto
            ) {
        return ResponseEntity.ok(postService.savePost(authUser, dto));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping("/posts")
    public ResponseEntity<PaginationResponse<PostResponse>> getAll(
            Pageable pageable,
            @RequestParam(required = false) String sort
    ) {
        return ResponseEntity.ok(postService.getAll(pageable, sort));
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Void> updatePost(
            @Auth AuthUser authUser,
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest dto
    ) {
        postService.updatePost(authUser, postId, dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @Auth AuthUser authUser,
            @PathVariable Long postId
    ) {
        postService.deletePost(authUser, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/posts/following")
    public ResponseEntity<PaginationResponse<PostResponse>> getFollowingPosts(
            @Auth AuthUser authUser,
            Pageable pageable
    ){
        return ResponseEntity.ok(postService.getFollowingPosts(authUser, pageable));
    }
}
