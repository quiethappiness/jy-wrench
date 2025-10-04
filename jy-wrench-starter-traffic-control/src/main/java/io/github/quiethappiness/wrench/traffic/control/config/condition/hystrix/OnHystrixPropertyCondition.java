package io.github.quiethappiness.wrench.traffic.control.config.condition.hystrix;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

// 条件1：检查YAML配置
@Slf4j
public class OnHystrixPropertyCondition extends SpringBootCondition
{
	String propertyName = "jy.wrench.traffic.hystrix.enabled";
	
	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata)
	{
		// 从环境变量中读取配置，提供默认值 false
		boolean enabled = context.getEnvironment()
			.getProperty(propertyName, Boolean.class, false);
		if (enabled)
		{
			// log.info("通过YAML配置启用了 Hystrix 功能");
			return ConditionOutcome.match("通过YAML配置启用了 Hystrix 功能");
		}
		return ConditionOutcome.noMatch("未在YAML配置中启用 Hystrix 功能");
	}
}
