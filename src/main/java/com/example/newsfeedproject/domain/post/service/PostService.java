package com.example.newsfeedproject.domain.post.service;

import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.common.pagination.PaginationResponse;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.post.dto.PostResponse;
import com.example.newsfeedproject.domain.post.dto.PostSaveRequest;
import com.example.newsfeedproject.domain.post.dto.PostSaveResponse;
import com.example.newsfeedproject.domain.post.entity.Post;
import com.example.newsfeedproject.domain.post.repository.PostRepository;
import com.example.newsfeedproject.domain.user.entity.User;
import com.example.newsfeedproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostSaveResponse savePost(AuthUser authUser, PostSaveRequest dto) {
        User user = userRepository.findById(authUser.getUserId()).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND, "User not found")
        );

        Post post = new Post(user, dto.getContent());
        Post savedPost = postRepository.save(post);

        return new PostSaveResponse(savedPost.getPostId());
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ExceptionType.POST_NOT_FOUND, "Post not found")
        );

        return new PostResponse(post.getPostId(), post.getContent(), post.getUsername(), post.getCreatedAt(), post.getUpdatedAt());
    }

    @Transactional(readOnly = true)
    public PaginationResponse<PostResponse> getAll(Pageable pageable) {

        Pageable tenPostsPerPage = PageRequest.of(pageable.getPageNumber(), 10, Sort.by(Sort.Order.desc("createdAt")));

        return new PaginationResponse<>(postRepository.findAll(tenPostsPerPage)
                .map(post -> new PostResponse(
                                post.getPostId(),
                                post.getContent(),
                                post.getUsername(),
                                post.getCreatedAt(),
                                post.getUpdatedAt()
                        )
                )
        );
    }

}
