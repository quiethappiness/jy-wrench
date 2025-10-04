package io.github.quiethappiness.method.extention.domain.service.me;

import com.alibaba.fastjson.JSON;
import io.github.quiethappiness.method.extention.type.annotations.MeMethodExtension;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static io.github.quiethappiness.aop.util.WrenchAopUtil.getTargetMethodFromJP;

@Aspect
@Component
// @ConditionalOnBean(RateLimiterAOP.class)
@Order(1)
@Slf4j
public class MethodExtensionAOP extends AbstractMethodExtensionAOP
{
	@Around(value = "methodExtensionPointcut() &&@annotation(methodExtension)")
	public Object doMethodExtension(ProceedingJoinPoint jp, MeMethodExtension methodExtension) throws Throwable
	{
		Method method = getTargetMethodFromJP(jp);
		Object proceed = null;
		// 1. 前置处理
		if (doBefore(jp, methodExtension))
		{
			return JSON.parseObject(methodExtension.beforeReturnJson(), method.getReturnType());
		}
		// 2. 执行业务方法
		try
		{
			proceed = jp.proceed(); // 核心：执行目标方法
			// 4. 返回后处理 (仅在成功返回后执行)
			doExec(jp, methodExtension.afterReturnMethod(), "MethodExtensionAOP: afterReturnMethod invoke error");
		}
		catch (Throwable throwable)
		{
			log.error("MethodExtensionAOP: business method invoke error", throwable);
			// 2.1 异常处理
			doExec(jp, methodExtension.afterThrowingMethod(), "MethodExtensionAOP: afterThrowingMethod invoke error");
			// 重要决策点：是返回默认值还是抛出异常？
			// 方案一（容灾）：返回默认值
			// return JSON.parseObject(methodExtension.afterReturnJson(), method.getReturnType());
			// 方案二（透传）：将异常包装或直接抛出
			throw throwable; // 推荐，除非有强容灾需求
		}
		finally
		{
			// 3. 后置处理 (确保执行)
			doExec(jp, methodExtension.afterMethod(), "MethodExtensionAOP: afterMethod invoke error");
		}
		
		return proceed; // 返回业务方法执行结果
	}
	
	
}
