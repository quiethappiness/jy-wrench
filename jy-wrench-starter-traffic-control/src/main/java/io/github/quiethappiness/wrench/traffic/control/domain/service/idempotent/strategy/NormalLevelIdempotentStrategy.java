package io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.strategy;

import io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.business.IIdempotentCheck;
import io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.business.IIdempotentToken;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcIdempotent;
import io.github.quiethappiness.wrench.traffic.control.types.exception.IdempotentException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

import static io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.getTargetMethodFromJP;

/**
 * FastLevelIdempotentStrategy
 * @description 常用层级的幂等性保证
 * @author quietHappiness @jingyue
 * @date 2025/11/3 17:17
 * @version 1.0
 */
@Service
@Slf4j
public class NormalLevelIdempotentStrategy implements IdempotentStrategy
{
	@Resource
	protected IIdempotentToken idempotentToken;
	@Resource
	protected IIdempotentCheck idempotentCheck;
	
	@Override
	public Object tokenCheck(ProceedingJoinPoint joinPoint, TcIdempotent idempotent, String token) throws NoSuchMethodException
	{
		if (!idempotentToken.checkAndMarkToken(token))
		{
			// 令牌已使用，返回之前的结果
			Method method = getTargetMethodFromJP(joinPoint);
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
		String businessType = IdempotentStrategy.spliceBusinessType(joinPoint);
		if (idempotentCheck.isRequestTooFrequent(businessType, idempotent))
		{
			// 释放令牌，允许重试
			idempotentToken.preReleaseToken(token);
			throw new IdempotentException("操作过于频繁，请稍后再试");
		}
	}
	
	@Override
	public void similarCheck(ProceedingJoinPoint joinPoint, String token) throws NoSuchMethodException
	{
	
	}
	
	@Override
	public Object execJoinPoint(ProceedingJoinPoint joinPoint, String token) throws Throwable
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