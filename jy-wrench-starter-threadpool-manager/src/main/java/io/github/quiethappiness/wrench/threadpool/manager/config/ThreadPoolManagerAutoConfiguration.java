package io.github.quiethappiness.wrench.threadpool.manager.config;

import io.github.quiethappiness.wrench.threadpool.manager.config.configuration.ThreadPoolManagerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
// @EnableAspectJAutoProxy
@Import(ThreadPoolManagerConfiguration.class)
public class ThreadPoolManagerAutoConfiguration
{

}
