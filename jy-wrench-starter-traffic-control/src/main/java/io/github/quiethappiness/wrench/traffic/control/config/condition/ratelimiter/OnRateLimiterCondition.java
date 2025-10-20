package io.github.quiethappiness.wrench.traffic.control.config.condition.ratelimiter;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// 组合条件：YAML配置 或 注解 任一满足即可
public class OnRateLimiterCondition extends AnyNestedCondition
{
	
	public OnRateLimiterCondition()
	{
		super(ConfigurationPhase.PARSE_CONFIGURATION);
	}
	
	@Conditional(OnRateLimiterPropertyCondition.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface OnPropertyAvailable {}
	
	@Conditional(OnRateLimiterAnnotationCondition.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface OnAnnotationAvailable {}
}
