package io.github.quiethappiness.wrench.util.mq.listener.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnProperty(prefix = "jy.wrench.util.mq-listener", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({RabbitMQProperties.class})
@Import(RabbitMQConfig.class)
public class MQListenerConfiguration
{
}
