package io.github.quiethappiness.wrench.traffic.control.config.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "jy.wrench.traffic.ee.enabled", havingValue = "true")
@ComponentScan(basePackages = {"io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit"})
@Slf4j
public class RateLimiterConfiguration
{
}
