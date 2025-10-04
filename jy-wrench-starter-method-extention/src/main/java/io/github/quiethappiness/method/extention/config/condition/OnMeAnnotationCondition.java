package io.github.quiethappiness.method.extention.config.condition;

import io.github.quiethappiness.method.extention.type.annotations.enable.EnableMethodExtension;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;

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
	
	/**
	 * 判断是否为配置类（包含@Configuration注解）
	 */
	private boolean isConfigurationClass(AnnotatedTypeMetadata metadata)
	{
		return metadata.isAnnotated("org.springframework.boot.autoconfigure.SpringBootApplication")
			|| metadata.isAnnotated("org.springframework.boot.autoconfigure.EnableAutoConfiguration")
			|| metadata.isAnnotated("org.springframework.context.annotation.Configuration");
	}
	
	/**
	 * 在所有已注册的Bean中检查是否存在指定注解
	 */
	private boolean isAnnotationPresentOnAnyBean(ConditionContext context, Class<? extends Annotation> annotationClass)
	{
		try
		{
			// 获取所有已注册的Bean定义名称
			String[] beanNames = context.getRegistry()
				.getBeanDefinitionNames();
			for (String beanName : beanNames)
			{
				BeanDefinition beanDef = context.getRegistry()
					.getBeanDefinition(beanName);
				if (beanDef instanceof AnnotatedBeanDefinition annotatedDef)
				{
					AnnotationMetadata annMetadata = annotatedDef.getMetadata();
					// 检查是否为配置类且带有指定注解
					if (isConfigurationClass(annMetadata) && annMetadata.isAnnotated(annotationClass.getName()))
					{
						return true;
					}
				}
			}
		}
		catch (Exception e)
		{
			log.warn("检查Bean注解时发生异常: {}", e.getMessage());
		}
		return false;
	}
}