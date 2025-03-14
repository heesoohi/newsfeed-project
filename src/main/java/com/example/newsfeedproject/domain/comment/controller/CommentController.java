package com.example.newsfeedproject.domain.comment.controller;

import com.example.newsfeedproject.common.annotation.Auth;
import com.example.newsfeedproject.common.pagination.PaginationResponse;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.comment.dto.request.CommentRequest;
import com.example.newsfeedproject.domain.comment.dto.response.CommentResponse;
import com.example.newsfeedproject.domain.comment.dto.response.CommentSaveResponse;
import com.example.newsfeedproject.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<PaginationResponse<CommentResponse>> getAll(
            Pageable pageable,
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(commentService.findAll(pageable, postId));
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @Auth AuthUser authUser,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest dto
    ) {
        commentService.updateComment(authUser, postId, commentId, dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @DeleteMapping("/posts/{postId}/comments/{commentId}")
//    public ResponseEntity<Void> deleteComment(
//            @Auth AuthUser authUser,
//            @PathVariable Long postId,
//            @PathVariable Long commentId
//    ) {
//        commentService.deleteComment(authUser, postId, commentId);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
}
