package io.github.quiethappiness.wrench.db.router.config.configuration;

import io.github.quiethappiness.wrench.db.router.config.bean.DBRouterProperty;
import io.github.quiethappiness.wrench.db.router.config.bean.DataSourceEnvironment;
import io.github.quiethappiness.wrench.db.router.config.bean.DynamicRoutingDataSource;
import io.github.quiethappiness.wrench.db.router.config.property.DBRouterProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "jy.wrench.config.db-router.jdbc.datasource", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan("io.github.quiethappiness.wrench.db.router.domain")
@EnableConfigurationProperties(DBRouterProperties.class)
@Import({DataSourceEnvironment.class, DBRouterProperty.class, DynamicRoutingDataSource.class})
public class DBRouterConfiguration
{
	{
		log.info("已启用 DB Router 配置");
	}
}
