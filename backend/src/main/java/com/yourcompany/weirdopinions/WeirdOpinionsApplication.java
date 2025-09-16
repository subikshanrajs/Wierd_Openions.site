package com.yourcompany.weirdopinions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WeirdOpinionsApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeirdOpinionsApplication.class, args);
    }
}