package io.github.quiethappiness.wrench.util.mq.listener.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(MQListenerConfiguration.class)
public class MQListenerAutoConfiguration
{
}
