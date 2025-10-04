package io.github.quiethappiness.wrench.traffic.control.config.condition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;

@Slf4j
public class ConditionUtil
{
	/**
	 * 判断是否为配置类（包含@Configuration注解）
	 */
	public static boolean isConfigurationClass(AnnotatedTypeMetadata metadata)
	{
		return metadata.isAnnotated("org.springframework.boot.autoconfigure.SpringBootApplication")
			|| metadata.isAnnotated("org.springframework.boot.autoconfigure.EnableAutoConfiguration")
			|| metadata.isAnnotated("org.springframework.context.annotation.Configuration");
	}
	
	/**
	 * 在所有已注册的Bean中检查是否存在指定注解
	 */
	public static boolean isAnnotationPresentOnAnyBean(ConditionContext context, Class<? extends Annotation> annotationClass)
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
