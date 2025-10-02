package io.github.quiethappiness.wrencher;

import io.github.quiethappiness.wrench.traffic.control.types.annotations.enable.EnableRateLimiter;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.enable.EnableWhiteList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// @Configurable
@SpringBootApplication
@EnableScheduling
@EnableRateLimiter
@EnableWhiteList
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

}