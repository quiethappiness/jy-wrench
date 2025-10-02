package io.github.quiethappiness.wrench.traffic.control.config.property;

import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ConditionalOnProperty(name = "jy.wrench.traffic.whitelist.enabled", havingValue = "true", matchIfMissing = false)
@ConfigurationProperties(prefix = "jy.wrench.traffic.whitelist", ignoreInvalidFields = true)
@Data
public class WhiteListProperties
{
	private boolean enabled = true;
	private Rule[] rules;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
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
