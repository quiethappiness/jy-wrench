package io.github.quiethappiness.method.extention.domain.service.me;

import io.github.quiethappiness.method.extention.domain.service.IMethodExtensionAOP;
import io.github.quiethappiness.method.extention.type.annotations.MeMethodExtension;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

@Slf4j
public abstract class AbstractMethodExtensionAOP implements IMethodExtensionAOP
{
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
	
	protected static void doExec(ProceedingJoinPoint jp, String methodExtension, String msg)
	{
		if (!StringUtils.hasText(methodExtension))
		{
			return;
		}
		try
		{
			Method afterThrowingMethod = getFallBackMethodFromJP(jp, methodExtension);
			afterThrowingMethod.invoke(jp.getThis(), jp.getArgs());
		}
		catch (Exception e)
		{
			log.error(msg, e);
		}
	}
	
	protected static boolean doBefore(ProceedingJoinPoint jp, MeMethodExtension methodExtension)
	{
		if (StringUtils.hasText(methodExtension.beforeMethod()))
		{
			try
			{
				Method beforeMethod = getFallBackMethodFromJP(jp, methodExtension.beforeMethod());
				beforeMethod.invoke(jp.getThis(), jp.getArgs());
			}
			catch (Exception e)
			{
				log.error("MethodExtensionAOP: beforeMethod invoke error", e);
				// 如果前置处理失败，直接返回默认值，不再执行业务方法
				return true;
			}
		}
		return false;
	}
}
