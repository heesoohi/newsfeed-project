package com.example.newsfeedproject.domain.comment.service;

import com.example.newsfeedproject.common.annotation.Auth;
import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.common.pagination.PaginationResponse;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.comment.dto.request.CommentRequest;
import com.example.newsfeedproject.domain.comment.dto.response.CommentResponse;
import com.example.newsfeedproject.domain.comment.dto.response.CommentSaveResponse;
import com.example.newsfeedproject.domain.comment.entity.Comment;
import com.example.newsfeedproject.domain.comment.repository.CommentRepository;
import com.example.newsfeedproject.domain.post.entity.Post;
import com.example.newsfeedproject.domain.post.repository.PostRepository;
import com.example.newsfeedproject.domain.user.entity.User;
import com.example.newsfeedproject.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
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

    @Transactional(readOnly = true)
    public PaginationResponse<CommentResponse> findAll(Pageable pageable, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ExceptionType.POST_NOT_FOUND, "Post not found")
        );

        Pageable tenCommentsPerPage = PageRequest.of(pageable.getPageNumber(), 10);

        return new PaginationResponse<>(
                commentRepository.findAll(tenCommentsPerPage)
                        .map(comment -> new CommentResponse(
                                comment.getCommentId(),
                                comment.getContent(),
                                comment.getPostId(comment),
                                comment.getUsername(comment),
                                comment.getCreatedAt(),
                                comment.getUpdatedAt()
                        ))
        );
    }

    @Transactional
    public void updateComment(
            @Auth AuthUser authUser,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest dto
    ) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ExceptionType.POST_NOT_FOUND, "Post not found")
        );

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(ExceptionType.COMMENT_NOT_FOUND, "Comment not found")
        );

        if (!authUser.getUserId().equals(comment.getUserId())) {
            throw new CustomException(ExceptionType.NO_PERMISSION_ACTION, "You do not have permission to update this comment");
        }

        comment.update(dto.getContent());
    }
}
