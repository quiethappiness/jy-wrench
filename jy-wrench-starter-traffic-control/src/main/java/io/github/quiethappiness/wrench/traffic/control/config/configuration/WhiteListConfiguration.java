package io.github.quiethappiness.wrench.traffic.control.config.configuration;

import io.github.quiethappiness.wrench.traffic.control.config.condition.OnWhiteListCondition;
import io.github.quiethappiness.wrench.traffic.control.config.property.WhiteListProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Conditional(OnWhiteListCondition.class)
@ComponentScan(basePackages = {"io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist"})
@EnableConfigurationProperties({WhiteListProperties.class})
@Configuration
@Slf4j
public class WhiteListConfiguration
{
	{
		log.info("已启用 white list 功能");
	}
}