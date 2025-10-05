package io.github.quiethappiness.wrench.method.extention.config.condition;

import io.github.quiethappiness.wrench.method.extention.type.annotations.enable.EnableMethodExtension;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.isAnnotationPresentOnAnyBean;
import static io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.isConfigurationClass;

// 条件2：检查是否存在@EnableMethodExtension注解
@Slf4j
public class OnMeAnnotationCondition extends SpringBootCondition
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