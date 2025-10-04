package io.github.quiethappiness.wrench.traffic.control.config.condition.hystrix;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// 组合条件：YAML配置 或 注解 任一满足即可
public class OnHystrixCondition extends AnyNestedCondition
{
	
	public OnHystrixCondition()
	{
		super(ConfigurationPhase.REGISTER_BEAN);
	}
	
	@Conditional(OnHystrixPropertyCondition.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface OnPropertyAvailable {}
	
	@Conditional(OnHystrixAnnotationCondition.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface OnAnnotationAvailable {}
}
