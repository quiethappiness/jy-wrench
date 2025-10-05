package io.github.quiethappiness.wrench.aop.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


public interface WrenchAopUtil
{
	Logger log = LoggerFactory.getLogger(WrenchAopUtil.class);
	/**
	 * 执行降级方法并返回结果
	 * @param jp
	 * 	连接点对象，包含目标方法的签名和执行信息
	 * @param fallbackMethod
	 * 	降级方法名称
	 * @return 降级方法执行后的返回结果
	 * @throws Exception
	 * 	当方法调用过程中发生异常时抛出
	 */
	static Object fallbackMethodResult(JoinPoint jp, String fallbackMethod) throws Exception
	{
		if (!StringUtils.hasText(fallbackMethod))
		{
			return "fallbackMethodResult";
		}
		Method method;
		try
		{
			method = getFallBackMethodFromJP(jp, fallbackMethod);
			// invoke() 方法用于执行指定对象上的方法
			return method.invoke(jp.getThis(), jp.getArgs());
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
			return "fallbackMethodResult";
		}
	}
	
	static Method getFallBackMethodFromJP(JoinPoint jp, String fallbackMethod) throws NoSuchMethodException
	{
		// 获取目标方法的签名信息
		// 获取被拦截方法的签名信息
		// Signature 是 AspectJ 中的一个接口，表示程序中各种元素（如方法、构造函数、字段等）的签名
		// 它包含了被拦截元素的基本信息，如名称、声明类型等
		// 可以确保找到的方法签名与原始方法一致，从而正确地调用降级处理方法
		Signature sig = jp.getSignature();
		// 将通用的 Signature 对象转换为 MethodSignature 类型
		// MethodSignature 是 AspectJ 提供的一个接口，专门用于表示方法级别的签名信息
		// 通过转换为 MethodSignature，可以获得更详细的方法信息，如：
		// - 方法参数类型列表
		// - 返回值类型
		// - 方法名称等
		MethodSignature methodSignature = (MethodSignature) sig;
		// 通过反射获取降级方法并执行
		Method method = jp.getTarget()
			.getClass()
			.getMethod(fallbackMethod, methodSignature.getParameterTypes());
		return method;
	}
	
	static Method getTargetMethodFromJP(JoinPoint jp) throws NoSuchMethodException
	{
		// 获取目标方法的签名信息
		// 获取被拦截方法的签名信息
		// Signature 是 AspectJ 中的一个接口，表示程序中各种元素（如方法、构造函数、字段等）的签名
		// 它包含了被拦截元素的基本信息，如名称、声明类型等
		// 可以确保找到的方法签名与原始方法一致，从而正确地调用降级处理方法
		Signature sig = jp.getSignature();
		// 将通用的 Signature 对象转换为 MethodSignature 类型
		// MethodSignature 是 AspectJ 提供的一个接口，专门用于表示方法级别的签名信息
		// 通过转换为 MethodSignature，可以获得更详细的方法信息，如：
		// - 方法参数类型列表
		// - 返回值类型
		// - 方法名称等
		MethodSignature methodSignature = (MethodSignature) sig;
		// 通过反射获取降级方法并执行
		Method method = jp.getTarget()
			.getClass()
			.getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
		return method;
	}
	
	/**
	 * 实际根据自身业务调整，主要是为了获取通过某个值做拦截
	 */
	static String getAttrValue(String attrName, Object[] args)
	{
		if (args[0] instanceof String)
		{
			return args[0].toString();
		}
		String filedValue = null;
		// 在多个参数中查找包含目标属性的对象
		for (Object arg : args)
		{
			try
			{
				if (org.apache.commons.lang.StringUtils.isNotBlank(filedValue))
				{
					break;
				}
				// filedValue = BeanUtils.getProperty(arg, attrName);
				// fix: 使用lombok时，uId这种字段的get方法与idea生成的get方法不同，会导致获取不到属性值，改成反射获取解决
				filedValue = String.valueOf(getValueByFieldName(arg, attrName));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return filedValue;
	}
	
	/**
	 * 获取对象的特定属性值
	 * @param bean
	 * 	对象
	 * @param name
	 * 	属性名
	 * @return 属性值
	 * @author tang
	 */
	static Object getValueByFieldName(Object bean, String name)
	{
		try
		{
			Field field = getFieldByName(bean, name);
			if (field == null)
			{
				return null;
			}
			field.setAccessible(true);
			Object o = field.get(bean);
			field.setAccessible(false);
			return o;
		}
		catch (IllegalAccessException e)
		{
			return null;
		}
	}
	
	/**
	 * 根据名称获取方法，该方法同时兼顾继承类获取父类的属性
	 * @param bean
	 * 	对象
	 * @param name
	 * 	属性名
	 * @return 该属性对应方法
	 * @author tang
	 */
	static Field getFieldByName(Object bean, String name)
	{
		try
		{
			Field field;
			try
			{
				field = bean.getClass()
					.getDeclaredField(name);
			}
			catch (NoSuchFieldException e)
			{
				field = bean.getClass()
					.getSuperclass()
					.getDeclaredField(name);
			}
			return field;
		}
		catch (NoSuchFieldException e)
		{
			return null;
		}
	}
	
	/**
	 * 判断是否为配置类（包含@Configuration注解）
	 */
	static boolean isConfigurationClass(AnnotatedTypeMetadata metadata)
	{
		return metadata.isAnnotated("org.springframework.boot.autoconfigure.SpringBootApplication")
			|| metadata.isAnnotated("org.springframework.boot.autoconfigure.EnableAutoConfiguration")
			|| metadata.isAnnotated("org.springframework.context.annotation.Configuration");
	}
	
	/**
	 * 在所有已注册的Bean中检查是否存在指定注解
	 */
	static boolean isAnnotationPresentOnAnyBean(ConditionContext context, Class<? extends Annotation> annotationClass)
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
