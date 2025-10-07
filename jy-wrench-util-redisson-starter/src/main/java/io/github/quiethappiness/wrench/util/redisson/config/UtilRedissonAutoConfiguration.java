package io.github.quiethappiness.wrench.util.redisson.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RedissonClientRegisterConfiguration.class})
@ComponentScan("io.github.quiethappiness.wrench.util.redisson.domain")
@Slf4j
public class UtilRedissonAutoConfiguration
{
}