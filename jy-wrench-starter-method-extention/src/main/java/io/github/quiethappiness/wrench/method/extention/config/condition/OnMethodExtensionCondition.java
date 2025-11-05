package io.github.quiethappiness.wrench.method.extention.config.condition;

import io.github.quiethappiness.wrench.method.extention.type.annotations.enable.EnableMethodExtension;
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
import static io.github.quiethappiness.wrench.method.extention.config.property.MethodExtensionProperties.JY_WRENCH_CONFIG_METHODE;

// 组合条件：YAML配置 或 注解 任一满足即可
public class OnMethodExtensionCondition extends AnyNestedCondition
{
	
	public OnMethodExtensionCondition()
	{
		super(ConfigurationPhase.PARSE_CONFIGURATION);
	}
	
	@Conditional(OnMePropertyCondition.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface OnPropertyAvailable {}
	
	@Conditional(OnMeAnnotationCondition.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface OnAnnotationAvailable {}
	
	// 条件1：检查YAML配置
	@Slf4j
	private static class OnMePropertyCondition extends SpringBootCondition
	{
		
		String enableValue = JY_WRENCH_CONFIG_METHODE+".enabled";
		
		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata)
		{
			// 从环境变量中读取配置，提供默认值 true
			boolean enabled = context.getEnvironment()
				.getProperty(enableValue, Boolean.class, true);
			if (enabled)
			{
				// log.info("通过YAML配置启用了MethodExtension功能");
				return ConditionOutcome.match("通过YAML配置启用了MethodExtension功能");
			}
			return ConditionOutcome.noMatch("未在YAML配置中启用MethodExtension功能");
		}
	}
	
	// 条件2：检查是否存在@EnableMethodExtension注解
	@Slf4j
	private static class OnMeAnnotationCondition extends SpringBootCondition
	{
		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata)
		{
			// 只有在处理配置类时才检查@EnableMethodExtension注解
			if (isConfigurationClass(metadata))
			{
				if (metadata.isAnnotated(EnableMethodExtension.class.getName()))
				{
					// log.info("检测到@EnableMethodExtension注解");
					return ConditionOutcome.match("检测到@EnableMethodExtension注解");
				}
			}
			// 遍历已注册的Bean查找是否有被@EnableMethodExtension标记的配置类
			if (isAnnotationPresentOnAnyBean(context, EnableMethodExtension.class))
			{
				// log.info("在已注册的Bean中检测到@EnableMethodExtension注解");
				return ConditionOutcome.match("在已注册的Bean中检测到@EnableMethodExtension注解");
			}
			return ConditionOutcome.noMatch("未检测到@EnableMethodExtension注解");
		}
		
	
	}
}