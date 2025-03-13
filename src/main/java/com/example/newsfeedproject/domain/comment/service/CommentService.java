package com.example.newsfeedproject.domain.comment.service;

import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.comment.dto.request.CommentRequest;
import com.example.newsfeedproject.domain.comment.dto.response.CommentSaveResponse;
import com.example.newsfeedproject.domain.comment.entity.Comment;
import com.example.newsfeedproject.domain.comment.repository.CommentRepository;
import com.example.newsfeedproject.domain.post.entity.Post;
import com.example.newsfeedproject.domain.post.repository.PostRepository;
import com.example.newsfeedproject.domain.user.entity.User;
import com.example.newsfeedproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentSaveResponse saveComment(AuthUser authUser, Long postId, CommentRequest dto) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ExceptionType.POST_NOT_FOUND, "Post not found")
        );

        User user = userRepository.findById(authUser.getUserId()).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND, "User not found")
        );

        Comment comment = new Comment(post, user, dto.getContent());

        Comment savedComment = commentRepository.save(comment);

        return new CommentSaveResponse(savedComment.getCommentId());
    }
}
