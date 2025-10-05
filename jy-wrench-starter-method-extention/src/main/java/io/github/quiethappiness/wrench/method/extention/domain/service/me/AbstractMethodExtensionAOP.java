package io.github.quiethappiness.wrench.method.extention.domain.service.me;

import io.github.quiethappiness.wrench.method.extention.domain.service.IMethodExtensionAOP;
import io.github.quiethappiness.wrench.method.extention.type.annotations.MeMethodExtension;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

import static io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.getFallBackMethodFromJP;

@Slf4j
public abstract class AbstractMethodExtensionAOP implements IMethodExtensionAOP
{
	
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
