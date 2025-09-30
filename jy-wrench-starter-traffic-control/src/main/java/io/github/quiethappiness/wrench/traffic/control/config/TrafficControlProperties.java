package io.github.quiethappiness.wrench.traffic.control.config;

import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ConditionalOnProperty(name = "jy.wrench.traffic.enabled", havingValue = "true")
@ConfigurationProperties(prefix = "jy.wrench.traffic", ignoreInvalidFields = true)
@Data
public class TrafficControlProperties
{
	private boolean enabled = true;
	private Rule[] rules;
	
	@Data
	public static class Rule
	{
		/**
		 * 进行ant风格匹配
		 */
		private String uri;
		private Integer limit;
		private TimeUnit timeUnit;
		private Map<WhiteListType, List<String>> whiteList;
	}
}
