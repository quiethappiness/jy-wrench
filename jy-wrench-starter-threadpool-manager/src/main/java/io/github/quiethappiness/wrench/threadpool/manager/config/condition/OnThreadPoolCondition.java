package io.github.quiethappiness.wrench.threadpool.manager.config.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// 组合条件：YAML配置 或 注解 任一满足即可
public class OnThreadPoolCondition extends AnyNestedCondition
{
	
	public OnThreadPoolCondition()
	{
		super(ConfigurationPhase.REGISTER_BEAN);
	}
	
	@Conditional(OnThreadPoolPropertyCondition.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface OnPropertyAvailable {}
	
	@Conditional(OnThreadPoolAnnotationCondition.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface OnAnnotationAvailable {}
}
