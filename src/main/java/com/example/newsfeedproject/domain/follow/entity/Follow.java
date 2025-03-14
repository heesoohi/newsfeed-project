package com.example.newsfeedproject.domain.follow.entity;

import com.example.newsfeedproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "follows")
public class Follow {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user")
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user")
    private User toUser;

    public Follow(User fromUser, User toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
    }
}
