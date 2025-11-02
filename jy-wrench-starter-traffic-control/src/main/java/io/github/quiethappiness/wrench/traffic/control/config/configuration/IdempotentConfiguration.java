package io.github.quiethappiness.wrench.traffic.control.config.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
// @Conditional(OnRateLimiterCondition.class)
@ComponentScan(basePackages = {"io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent"})
// @EnableConfigurationProperties({RateLimiterProperties.class})
@Slf4j
public class IdempotentConfiguration
{
	
	{
		log.info("已启用 idempotent 功能");
	}
}