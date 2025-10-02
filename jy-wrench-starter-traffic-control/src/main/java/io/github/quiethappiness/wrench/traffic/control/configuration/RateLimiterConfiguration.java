package io.github.quiethappiness.wrench.traffic.control.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit"})
@Slf4j
public class RateLimiterConfiguration
{
}
