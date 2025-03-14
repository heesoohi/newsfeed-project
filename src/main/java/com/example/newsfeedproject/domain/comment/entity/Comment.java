package com.example.newsfeedproject.domain.comment.entity;

import com.example.newsfeedproject.common.entity.BaseTimeEntity;
import com.example.newsfeedproject.domain.post.entity.Post;
import com.example.newsfeedproject.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comments")
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false)
    private int likeCount = 0;

    public Comment(Post post, User user, String content) {
        this.post = post;
        this.user = user;
        this.content = content;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public Long getPostId(Comment comment) {
        return comment.getPost().getPostId();
    }

    public String getUsername(Comment comment) {
        return comment.getUser().getUsername();
    }

    public Long getUserId() {
        return user.getUserId();
    }

    public void update(String content) {
        this.content = content;
    }
}
