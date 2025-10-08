package io.github.quiethappiness.wrench.threadpool.manager.config.configuration;

import io.github.quiethappiness.wrench.threadpool.manager.config.condition.OnThreadPoolCondition;
import io.github.quiethappiness.wrench.threadpool.manager.config.property.ThreadPoolConfigProperties;
import io.github.quiethappiness.wrench.threadpool.manager.config.property.ThreadPoolManagerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Conditional(OnThreadPoolCondition.class) // 核心：应用“或”逻辑条件
@ComponentScan("io.github.quiethappiness.wrench.threadpool.manager.domain") // 扫描指定包下的类
@Import(ThreadPoolConfig.class)
// 引入配置类
@EnableConfigurationProperties({ThreadPoolManagerProperties.class, ThreadPoolConfigProperties.class})
@Slf4j
@Configuration
public class ThreadPoolManagerConfiguration
{
	{
		log.info("已启用 thread pool manager 扩展功能");
	}
}
