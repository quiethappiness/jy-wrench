package io.github.quiethappiness.wrench;

import io.github.quiethappiness.wrench.traffic.control.types.annotations.enable.EnableRateLimiter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configurable
@SpringBootApplication
@EnableScheduling
@EnableRateLimiter
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

}