package com.example.newsfeedproject.domain.like.post.service;

import com.example.newsfeedproject.common.exception.CustomException;
import com.example.newsfeedproject.common.exception.ExceptionType;
import com.example.newsfeedproject.domain.auth.dto.AuthUser;
import com.example.newsfeedproject.domain.like.post.entity.PostLike;
import com.example.newsfeedproject.domain.like.post.repository.PostLikeRepository;
import com.example.newsfeedproject.domain.post.entity.Post;
import com.example.newsfeedproject.domain.post.repository.PostRepository;
import com.example.newsfeedproject.domain.user.entity.User;
import com.example.newsfeedproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void likePost(AuthUser authUser, Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ExceptionType.POST_NOT_FOUND, "Post not found")
        );

        User user = userRepository.findById(authUser.getUserId()).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND, "User not found")
        );

        if(user.getUserId().equals(post.getUserId(postId))) {
            throw new CustomException(ExceptionType.SELF_LIKE_NOT_ALLOWED, "Self like not allowed");
        }

        Optional<PostLike> existingPostLike = postLikeRepository.findByPostAndUser(post, user);
        if (existingPostLike.isPresent()) {
            throw new CustomException(ExceptionType.ALREADY_LIKED, "Post already liked");
        }

        PostLike postLike = new PostLike(post, user);
        postLikeRepository.save(postLike);
    }


//    Optional<PostLike> existingPostLike = postLikeRepository.findByPostAndUser(post, user);
//        if (existingPostLike.isPresent()) {
//        postLikeRepository.delete(existingPostLike.get());
//        return; // 좋아요 취소
//    }
}
