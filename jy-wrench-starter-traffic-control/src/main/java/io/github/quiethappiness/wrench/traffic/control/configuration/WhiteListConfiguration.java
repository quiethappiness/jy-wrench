package io.github.quiethappiness.wrench.traffic.control.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@ComponentScan(basePackages = {"io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist"})
public class WhiteListConfiguration
{

}
