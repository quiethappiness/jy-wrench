package io.github.quiethappiness.wrench.traffic.control.config.condition;

import io.github.quiethappiness.wrench.traffic.control.types.annotations.enable.EnableHystrix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.isAnnotationPresentOnAnyBean;
import static io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.isConfigurationClass;
import static io.github.quiethappiness.wrench.traffic.control.config.property.HystrixProperties.JY_WRENCH_CONFIG_TRAFFIC_HYSTRIX_ENABLED;

// 组合条件：YAML配置 或 注解 任一满足即可
public class OnHystrixCondition extends AnyNestedCondition
{
	
	public OnHystrixCondition()
	{
		super(ConfigurationPhase.PARSE_CONFIGURATION);
	}
	
	@Conditional(OnHystrixPropertyCondition.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface OnPropertyAvailable {}
	
	@Conditional(OnHystrixAnnotationCondition.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface OnAnnotationAvailable {}
	
	// 条件1：检查YAML配置
	@Slf4j
	private static class OnHystrixPropertyCondition extends SpringBootCondition
	{
		
		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata)
		{
			// 从环境变量中读取配置，提供默认值 false
			boolean enabled = context
				.getEnvironment()
				.getProperty(JY_WRENCH_CONFIG_TRAFFIC_HYSTRIX_ENABLED, Boolean.class, true);
			if (enabled)
			{
				// log.info("通过YAML配置启用了 Hystrix 功能");
				return ConditionOutcome.match("通过YAML配置启用了 Hystrix 功能");
			}
			return ConditionOutcome.noMatch("未在YAML配置中启用 Hystrix 功能");
		}
	}
	
	// 条件2：检查是否存在EnableHystrix注解
	@Slf4j
	private static class OnHystrixAnnotationCondition extends SpringBootCondition
	{
		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata)
		{
			// 只有在处理配置类时才检查EnableHystrix注解
			if (isConfigurationClass(metadata))
			{
				if (metadata.isAnnotated(EnableHystrix.class.getName()))
				{
					// log.info("检测到EnableHystrix注解");
					return ConditionOutcome.match("检测到EnableHystrix注解");
				}
			}
			// 遍历已注册的Bean查找是否有被EnableHystrix标记的配置类
			if (isAnnotationPresentOnAnyBean(context, EnableHystrix.class))
			{
				// log.info("在已注册的Bean中检测到EnableHystrix注解");
				return ConditionOutcome.match("在已注册的Bean中检测到EnableHystrix注解");
			}
			return ConditionOutcome.noMatch("未检测到EnableHystrix注解");
		}
	}
	

}