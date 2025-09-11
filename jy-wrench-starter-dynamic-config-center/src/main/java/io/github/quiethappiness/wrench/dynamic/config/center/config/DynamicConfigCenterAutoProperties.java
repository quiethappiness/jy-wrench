package io.github.quiethappiness.wrench.dynamic.config.center.config;

import io.github.quiethappiness.wrench.dynamic.config.center.types.common.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * DynamicConfigCenterAutoProperties
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 动态配置
 * @date 2025/9/10 15:39
 */
@ConfigurationProperties(prefix = "jy.wrench.config",ignoreInvalidFields = true)
public class DynamicConfigCenterAutoProperties
{
	
	/**
	 * 系统名称
	 */
	private String system;
	
	public String getKey(String attributeName) {
		return this.system + Constants.LINE + attributeName;
	}
	
	public String getSystem() {
		return system;
	}
	
	public void setSystem(String system) {
		this.system = system;
	}
}