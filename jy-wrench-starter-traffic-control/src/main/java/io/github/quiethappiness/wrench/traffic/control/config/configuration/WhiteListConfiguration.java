package io.github.quiethappiness.wrench.traffic.control.config.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name = "jy.wrench.traffic.whitelist.enabled", havingValue = "true")
@Configuration
@Slf4j
@ComponentScan(basePackages = {"io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist"})
public class WhiteListConfiguration
{

}
