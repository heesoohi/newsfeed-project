package com.example.newsfeedproject.domain.post.dto;

import lombok.Getter;

@Getter
public class PostSaveResponse {

    private final Long postId;

    public PostSaveResponse(Long postId) {
        this.postId = postId;
    }
}
