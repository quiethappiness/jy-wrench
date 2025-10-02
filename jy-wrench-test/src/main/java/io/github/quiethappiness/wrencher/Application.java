package io.github.quiethappiness.wrencher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// @Configurable
@SpringBootApplication
@EnableScheduling
// @EnableRateLimiter
// @EnableWhiteList
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

}