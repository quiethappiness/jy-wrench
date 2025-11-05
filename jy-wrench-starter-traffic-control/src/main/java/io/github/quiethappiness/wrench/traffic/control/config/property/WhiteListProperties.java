package io.github.quiethappiness.wrench.traffic.control.config.property;

import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcWhiteList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ConfigurationProperties(prefix = WhiteListProperties.JY_WRENCH_CONFIG_TRAFFIC_WHITELIST, ignoreInvalidFields = true)
@Data
public class WhiteListProperties
{
	public static final String JY_WRENCH_CONFIG_TRAFFIC_WHITELIST = "jy.wrench.config.traffic.whitelist";
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
		private Map<TcWhiteList.WhiteListType, List<String>> whiteList;
	}
	public static final String JY_WRENCH_CONFIG_TRAFFIC_WHITELIST_ENABLED = JY_WRENCH_CONFIG_TRAFFIC_WHITELIST+".enabled";
}