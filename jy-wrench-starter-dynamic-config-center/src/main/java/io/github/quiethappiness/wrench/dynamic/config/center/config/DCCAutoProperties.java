package io.github.quiethappiness.wrench.dynamic.config.center.config;

import io.github.quiethappiness.wrench.dynamic.config.center.types.common.Constants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * DCCAutoProperties
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 动态配置
 * @date 2025/9/10 15:39
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "jy.wrench.config", ignoreInvalidFields = true)
// @AutoConfiguration
public class DCCAutoProperties
{
	
	/**
	 * 系统名称
	 */
	private String system;
	
	public String getKey(String attributeName)
	{
		return this.system + Constants.LINE + attributeName;
	}
}