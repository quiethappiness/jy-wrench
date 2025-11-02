package io.github.quiethappiness.wrench.traffic.control.config;

import io.github.quiethappiness.wrench.traffic.control.config.configuration.HystrixConfiguration;
import io.github.quiethappiness.wrench.traffic.control.config.configuration.IdempotentConfiguration;
import io.github.quiethappiness.wrench.traffic.control.config.configuration.RateLimiterConfiguration;
import io.github.quiethappiness.wrench.traffic.control.config.configuration.WhiteListConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * TrafficControlAutoConfiguration
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 限流切片的自动配置
 * @date 2025/9/12 15:41
 */
@Configuration
@EnableAspectJAutoProxy
// @ComponentScan(basePackages = "io.github.quiethappiness.wrench.traffic.control.config.configuration")
@Import({RateLimiterConfiguration.class, WhiteListConfiguration.class, HystrixConfiguration.class, IdempotentConfiguration.class})
public class TrafficControlAutoConfiguration
{
	// @Bean
	// public RateLimiterAOP rateLimiterAOP() {
	// 	log.info("RateLimiterAOP init...");
	// 	return new RateLimiterAOP();
	// }
}