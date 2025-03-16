package com.example.newsfeedproject.domain.user.repository;

import com.example.newsfeedproject.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private static final String TEST_EMAIL = "test@gmail.com";
    private static final String TEST_NAME = "name";
    private static final String TEST_PASSWORD = "password";

    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 객체를 초기화
        testUser = new User(TEST_EMAIL, TEST_NAME, TEST_PASSWORD);
    }

    @DisplayName("이메일로 사용자를 조회할 수 있다.")
    @Test
    void findUserByEmail() {
        // given
        User user = new User(TEST_EMAIL, TEST_NAME, TEST_PASSWORD);
        userRepository.save(user);
        
        // when
        Optional<User> foundUserOptional = userRepository.findByEmail(TEST_EMAIL);
        // then
        assertTrue(foundUserOptional.isPresent(), "사용자가 조회되지 않았습니다.");
        User foundUser = foundUserOptional.get();
        assertEquals(TEST_EMAIL, foundUser.getEmail());
        assertEquals(TEST_NAME, foundUser.getUsername());
        assertEquals(TEST_PASSWORD, foundUser.getPassword());
        
    }

}
