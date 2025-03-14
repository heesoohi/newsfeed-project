package com.example.newsfeedproject.domain.like.comment.service;

import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.comment.entity.Comment;
import com.example.newsfeedproject.domain.comment.repository.CommentRepository;
import com.example.newsfeedproject.domain.like.comment.entity.CommentLike;
import com.example.newsfeedproject.domain.like.comment.repository.CommentLikeRepository;
import com.example.newsfeedproject.domain.like.post.entity.PostLike;
import com.example.newsfeedproject.domain.user.entity.User;
import com.example.newsfeedproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public void likeComment(AuthUser authUser, Long postId, Long commentId) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(ExceptionType.COMMENT_NOT_FOUND)
        );

        User user = userRepository.findById(authUser.getUserId()).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );

        if(user.getUserId().equals(comment.getUserId())) {
            throw new CustomException(ExceptionType.SELF_LIKE_NOT_ALLOWED);
        }

        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentAndUser(comment, user);
        if (existingLike.isPresent()) {
            throw new CustomException(ExceptionType.ALREADY_LIKED);
        }

        CommentLike commentLike = new CommentLike(comment, user);
        commentLikeRepository.save(commentLike);
    }
}
