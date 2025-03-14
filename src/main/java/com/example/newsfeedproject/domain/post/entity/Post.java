package com.example.newsfeedproject.domain.post.entity;

import com.example.newsfeedproject.common.entity.BaseTimeEntity;
import com.example.newsfeedproject.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "posts")
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int likeCount = 0;

    public Post(User user, String content) {
        this.user = user;
        this.content = content;
    }

    public String getUsername() {
        return user.getUsername();
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void update(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return this.user.getUserId();
    }
}
