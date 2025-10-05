package io.github.quiethappiness.db.router.config.configuration;

import io.github.quiethappiness.db.router.config.property.DBRouterProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Slf4j
@ComponentScan("io.github.quiethappiness.db.router.domain.service")
@EnableConfigurationProperties(DBRouterProperties.class)
@Import({DataSourceEnvironment.class, DBRouterProperty.class, DynamicRoutingDataSource.class})
public class DBRouterConfiguration
{
	{
		log.info("已启用 DB Router 配置");
	}
}
