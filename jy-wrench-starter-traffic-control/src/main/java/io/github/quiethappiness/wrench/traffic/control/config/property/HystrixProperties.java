package io.github.quiethappiness.wrench.traffic.control.config.property;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConditionalOnProperty(name = "jy.wrench.traffic.hystrix.enabled", havingValue = "true",matchIfMissing = false)
@ConfigurationProperties(prefix = "jy.wrench.traffic.hystrix", ignoreInvalidFields = true)
@Data
public class HystrixProperties
{
	private boolean enabled = true;
}
