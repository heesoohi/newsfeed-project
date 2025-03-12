package com.example.newsfeedproject.domain.post.service;

import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.post.dto.PostResponse;
import com.example.newsfeedproject.domain.post.dto.PostSaveRequest;
import com.example.newsfeedproject.domain.post.dto.PostSaveResponse;
import com.example.newsfeedproject.domain.post.entity.Post;
import com.example.newsfeedproject.domain.post.repository.PostRepository;
import com.example.newsfeedproject.domain.user.entity.User;
import com.example.newsfeedproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostSaveResponse savePost(AuthUser authUser, PostSaveRequest dto) {
        User user = userRepository.findById(authUser.getUserId()).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND, "User not found")
        );

        Post post = new Post(user, dto.getContent());
        Post savedPost = postRepository.save(post);

        return new PostSaveResponse(savedPost.getPostId());
    }

    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ExceptionType.POST_NOT_FOUND, "Post not found")
        );

        return new PostResponse(post.getPostId(), post.getContent(), post.getUsername(), post.getCreatedAt(), post.getUpdatedAt());
    }
}
