package io.github.quiethappiness.wrench.traffic.control.config.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name = "jy.wrench.traffic.hystrix.enabled", havingValue = "true")
@Configuration
@Slf4j
@ComponentScan("io.github.quiethappiness.wrench.traffic.control.domain.service.hystrix")
public class HystrixConfiguration
{
}
