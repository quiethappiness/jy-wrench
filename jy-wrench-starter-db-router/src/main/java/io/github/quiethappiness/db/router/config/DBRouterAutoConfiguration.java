package io.github.quiethappiness.db.router.config;

import io.github.quiethappiness.db.router.config.property.DBRouterProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableConfigurationProperties(DBRouterProperties.class)
@Slf4j
@ComponentScan(basePackages = "io.github.quiethappiness.db.router.config.configuration")
@EnableAspectJAutoProxy
public class DBRouterAutoConfiguration
{
}
