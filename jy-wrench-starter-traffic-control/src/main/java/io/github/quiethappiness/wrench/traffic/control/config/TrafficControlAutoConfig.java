package io.github.quiethappiness.wrench.traffic.control.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * TrafficControlAutoConfig
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 限流切片的自动配置
 * @date 2025/9/12 15:41
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(TrafficControlProperties.class)
public class TrafficControlAutoConfig
{
	// @Bean
	// public RateLimiterAOP rateLimiterAOP() {
	// 	log.info("RateLimiterAOP init...");
	// 	return new RateLimiterAOP();
	// }
}