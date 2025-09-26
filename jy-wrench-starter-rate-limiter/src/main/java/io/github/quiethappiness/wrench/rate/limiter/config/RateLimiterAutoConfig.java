package io.github.quiethappiness.wrench.rate.limiter.config;

import io.github.quiethappiness.wrench.rate.limiter.domain.service.RateLimiterAOP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * RateLimiterAutoConfig
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 限流切片的自动配置
 * @date 2025/9/12 15:41
 */
@Configuration
@ComponentScan(value = {"io.github.quiethappiness.wrench.rate.limiter.domain.service"})
@Slf4j
public class RateLimiterAutoConfig
{
	@Bean
	public RateLimiterAOP rateLimiterAOP() {
		log.info("RateLimiterAOP init...");
		return new RateLimiterAOP();
	}
}