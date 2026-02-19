package com.example.amit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class ExamPortalBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamPortalBeApplication.class, args);
    }

}
