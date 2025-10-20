package io.github.quiethappiness.wrench.traffic.control.config.condition.whitelist;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// 组合条件：YAML配置 或 注解 任一满足即可
public class OnWhiteListCondition extends AnyNestedCondition
{
	
	public OnWhiteListCondition()
	{
		super(ConfigurationPhase.PARSE_CONFIGURATION);
	}
	
	@Conditional(OnWhiteListPropertyCondition.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface OnPropertyAvailable {}
	
	@Conditional(OnWhiteListAnnotationCondition.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface OnAnnotationAvailable {}
}
