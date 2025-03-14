package com.example.newsfeedproject.domain.like.post.entity;

import com.example.newsfeedproject.domain.post.entity.Post;
import com.example.newsfeedproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "post_likes")
public class PostLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public PostLike(Post post, User user) {
        this.post = post;
        this.user = user;
    }
}
