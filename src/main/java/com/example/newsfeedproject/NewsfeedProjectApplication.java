package com.example.newsfeedproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NewsfeedProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsfeedProjectApplication.class, args);
    }

}
