package io.github.quiethappiness.wrench.traffic.control.config;

import io.github.quiethappiness.wrench.traffic.control.config.property.RateLimiterProperties;
import io.github.quiethappiness.wrench.traffic.control.config.property.WhiteListProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * TrafficControlAutoConfig
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 限流切片的自动配置
 * @date 2025/9/12 15:41
 */
@Configuration
@Slf4j
@EnableConfigurationProperties({RateLimiterProperties.class, WhiteListProperties.class})
@ComponentScan(basePackages = "io.github.quiethappiness.wrench.traffic.control.config.configuration")
@EnableAspectJAutoProxy
// @ComponentScan(basePackages = "io.github.quiethappiness.wrench.traffic.control")
public class TrafficControlAutoConfig
{
	// @Bean
	// public RateLimiterAOP rateLimiterAOP() {
	// 	log.info("RateLimiterAOP init...");
	// 	return new RateLimiterAOP();
	// }
	{
		log.info("TrafficControlAutoConfig init...");
	}
}