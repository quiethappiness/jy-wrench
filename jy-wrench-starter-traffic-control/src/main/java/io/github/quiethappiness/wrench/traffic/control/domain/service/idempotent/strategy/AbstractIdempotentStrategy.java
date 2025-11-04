package io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.strategy;

import io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.MethodPart;
import io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.business.IIdempotentCheck;
import io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.business.IIdempotentToken;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcIdempotent;
import io.github.quiethappiness.wrench.traffic.control.types.exception.IdempotentException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

/**
 * AbstractIdempotentStrategy
 * @description 抽象实现
 * @author quietHappiness @jingyue
 * @date 2025/11/4 17:16
 * @version 1.0
 */
@Slf4j
public abstract class AbstractIdempotentStrategy implements IdempotentStrategy
{
	@Resource
	protected IIdempotentToken idempotentToken;
	@Resource
	protected IIdempotentCheck idempotentCheck;
	
	public static String spliceBusinessType(ProceedingJoinPoint joinPoint) throws NoSuchMethodException
	{
		Method method = MethodPart.getTargetMethodFromJP(joinPoint);
		return method
			.getDeclaringClass()
			.getSimpleName() + "." + method.getName();
	}
	
	@Override
	public Object tokenCheck(ProceedingJoinPoint joinPoint, TcIdempotent idempotent, String token) throws NoSuchMethodException
	{
		if (!idempotentToken.checkAndMarkToken(token))
		{
			// 令牌已使用，返回之前的结果
			Method method = MethodPart.getTargetMethodFromJP(joinPoint);
			Object previousResult = idempotentToken.getPreviousResult(token, method.getReturnType());
			if (previousResult != null)
			{
				return previousResult;
			}
			throw new IdempotentException(idempotent.message());
		}
		return null;
	}
	
	@Override
	public void frequencyCheck(ProceedingJoinPoint joinPoint, TcIdempotent idempotent, String token) throws NoSuchMethodException
	{
	
	}
	
	@Override
	public void similarCheck(ProceedingJoinPoint joinPoint, String token) throws NoSuchMethodException
	{
	
	}
	
	@Override
	public Object execJoinPoint(ProceedingJoinPoint joinPoint, TcIdempotent idempotent,String token) throws Throwable
	{
		try
		{
			Object result = joinPoint.proceed();
			idempotentToken.cacheResult(token, result);
			return result;
		}
		catch (Exception e)
		{
			// 业务执行失败，根据错误类型决定是否释放令牌
			log.error("业务执行失败", e);
			throw e;
		}
		finally
		{
			idempotentToken.preReleaseToken(token);
		}
	}
}