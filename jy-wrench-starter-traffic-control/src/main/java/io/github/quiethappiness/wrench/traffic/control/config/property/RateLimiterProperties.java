package io.github.quiethappiness.wrench.traffic.control.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = RateLimiterProperties.JY_WRENCH_CONFIG_TRAFFIC_RATELIMITER, ignoreInvalidFields = true)
@Data
public class RateLimiterProperties
{
	public static final String JY_WRENCH_CONFIG_TRAFFIC_RATELIMITER = "jy.wrench.config.traffic.ratelimiter";
	private boolean enabled = true;
	
	public static  final String JY_WRENCH_CONFIG_TRAFFIC_RATELIMITER_ENABLED = JY_WRENCH_CONFIG_TRAFFIC_RATELIMITER+".enabled";
}