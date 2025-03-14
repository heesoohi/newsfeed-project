package com.example.newsfeedproject.domain.comment.dto.response;

import lombok.Getter;

@Getter
public class CommentSaveResponse {

    private final Long commentId;

    public CommentSaveResponse(Long commentId) {
        this.commentId = commentId;
    }
}
