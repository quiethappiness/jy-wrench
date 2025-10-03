package io.github.quiethappiness.method.extention.config.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name = "jy.wrench.traffic.hystrix.enabled", havingValue = "true")
@Configuration
@Slf4j
@ComponentScan("io.github.quiethappiness.method.extention.domain")
public class MethodExtensionConfiguration
{
}
