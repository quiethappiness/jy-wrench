package io.github.quiethappiness.wrencher;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configurable
@SpringBootApplication
@EnableScheduling
// @EnableRateLimiter
// @EnableWhiteList
// @EnableMethodExtension
public class Application {
	
	public static void main(String[] args)
	{
		SpringApplication.run(Application.class);
	}
}