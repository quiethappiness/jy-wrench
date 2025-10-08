package io.github.quiethappiness.wrench.threadpool.manager.config.condition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

// 条件1：检查YAML配置
@Slf4j
public class OnThreadPoolPropertyCondition extends SpringBootCondition
{
	public static final String PROPERTY_PREFIX = "jy.wrench.config.thread.pool";

	String enableValue = PROPERTY_PREFIX+".enabled";
	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata)
	{
		// 从环境变量中读取配置，提供默认值 false
		boolean enabled = context.getEnvironment()
			.getProperty(enableValue, Boolean.class, true);
		if (enabled)
		{
			// log.info("通过YAML配置启用了MethodExtension功能");
			return ConditionOutcome.match("通过YAML配置启用了 thread pool manager 功能");
		}
		return ConditionOutcome.noMatch("未在YAML配置中启用 thread pool manager 功能");
	}
}
