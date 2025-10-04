package io.github.quiethappiness.wrench.traffic.control.config.configuration;

import io.github.quiethappiness.wrench.traffic.control.config.condition.ratelimiter.OnRateLimiterCondition;
import io.github.quiethappiness.wrench.traffic.control.config.property.RateLimiterProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(OnRateLimiterCondition.class)
@ComponentScan(basePackages = {"io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit"})
@EnableConfigurationProperties({RateLimiterProperties.class})
@Slf4j
public class RateLimiterConfiguration
{
	{
		log.info("已启用 Rate Limiter 功能");
	}
}
