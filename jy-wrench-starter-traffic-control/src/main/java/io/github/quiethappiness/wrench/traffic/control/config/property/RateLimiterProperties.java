package io.github.quiethappiness.wrench.traffic.control.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jy.wrench.config.traffic.ratemiter", ignoreInvalidFields = true)
@Data
public class RateLimiterProperties
{
	private boolean enabled = true;
}
