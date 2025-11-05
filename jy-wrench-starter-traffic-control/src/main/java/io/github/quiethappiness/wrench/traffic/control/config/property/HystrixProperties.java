package io.github.quiethappiness.wrench.traffic.control.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = HystrixProperties.JY_WRENCH_CONFIG_TRAFFIC_HYSTRIX, ignoreInvalidFields = true)
@Data
public class HystrixProperties
{
	public static final String JY_WRENCH_CONFIG_TRAFFIC_HYSTRIX = "jy.wrench.config.traffic.hystrix";
	private boolean enabled = true;
	
	public static final String JY_WRENCH_CONFIG_TRAFFIC_HYSTRIX_ENABLED = JY_WRENCH_CONFIG_TRAFFIC_HYSTRIX+".enabled";
}