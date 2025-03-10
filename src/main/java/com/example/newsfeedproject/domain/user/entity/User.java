package com.example.newsfeedproject.domain.user.entity;

import com.example.newsfeedproject.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 50)
    private String password;

    @Builder
    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
