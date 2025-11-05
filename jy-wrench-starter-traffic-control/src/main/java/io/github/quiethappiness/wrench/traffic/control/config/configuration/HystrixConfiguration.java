package io.github.quiethappiness.wrench.traffic.control.config.configuration;

import io.github.quiethappiness.wrench.traffic.control.config.condition.OnHystrixCondition;
import io.github.quiethappiness.wrench.traffic.control.config.property.HystrixProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Conditional(OnHystrixCondition.class)
@Configuration
@Slf4j
@ComponentScan("io.github.quiethappiness.wrench.traffic.control.domain.service.hystrix")
@EnableConfigurationProperties({ HystrixProperties.class})
public class HystrixConfiguration
{
	{
		log.info("已启用 Hystrix 功能");
	}
}