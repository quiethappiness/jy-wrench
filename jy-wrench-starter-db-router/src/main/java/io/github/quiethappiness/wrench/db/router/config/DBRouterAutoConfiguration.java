package io.github.quiethappiness.wrench.db.router.config;

import io.github.quiethappiness.wrench.db.router.config.configuration.DBRouterConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Configuration
@Slf4j
@EnableAspectJAutoProxy
@Import(DBRouterConfiguration.class)
public class DBRouterAutoConfiguration
{
}
