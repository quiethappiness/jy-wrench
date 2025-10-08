package io.github.quiethappiness.wrench.threadpool.manager.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.github.quiethappiness.wrench.threadpool.manager.config.condition.OnThreadPoolPropertyCondition.PROPERTY_PREFIX;

@ConfigurationProperties(prefix = PROPERTY_PREFIX, ignoreInvalidFields = true)
@Data
public class ThreadPoolManagerProperties
{
	
	private boolean enabled = true;
}
