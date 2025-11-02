package io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent;

import io.github.quiethappiness.wrench.traffic.control.domain.service.IIdempotentAOP;
import io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.business.IdempotentService;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcIdempotent;
import io.github.quiethappiness.wrench.traffic.control.types.exception.IdempotentException;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

import static io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.getTargetMethodFromJP;

/**
 * AbstractRateLimiterAop
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 抽象实现
 * @date 2025/9/12 15:54
 */
@Slf4j
public abstract class AbstractIdempotentAOP implements IIdempotentAOP
{
	@Resource
	protected IdempotentService tokenService;
	
	@Nullable
	protected Object tokenCheck(ProceedingJoinPoint joinPoint, TcIdempotent idempotent, String token) throws NoSuchMethodException
	{
		if (!tokenService.checkAndMarkToken(token))
		{
			// 令牌已使用，返回之前的结果
			Method method = getTargetMethodFromJP(joinPoint);
			Object previousResult = tokenService.getPreviousResult(token, method.getReturnType());
			if (previousResult != null)
			{
				return previousResult;
			}
			throw new IdempotentException(idempotent.message());
		}
		return null;
	}
	
	protected void frequencyCheck(ProceedingJoinPoint joinPoint, TcIdempotent idempotent, String token) throws NoSuchMethodException
	{
		String businessType = spliceBusinessType(joinPoint);
		if (tokenService.isRequestTooFrequent(businessType, idempotent.attrKey()))
		{
			// 释放令牌，允许重试
			tokenService.deleteToken(token);
			throw new IdempotentException("操作过于频繁，请稍后再试");
		}
	}
	
	private static String spliceBusinessType(ProceedingJoinPoint joinPoint) throws NoSuchMethodException
	{
		Method method = getTargetMethodFromJP(joinPoint);
		return method
			.getDeclaringClass()
			.getSimpleName() + "." + method.getName();
	}
	
	protected void similarCheck(ProceedingJoinPoint joinPoint, String token) throws NoSuchMethodException
	{
		if (tokenService.hasSimilarRecentRequest(joinPoint, spliceBusinessType(joinPoint)))
		{
			tokenService.deleteToken(token);
			throw new IdempotentException("检测到相似的近期操作");
		}
	}
	
	protected Object execJoinPoint(ProceedingJoinPoint joinPoint, String token) throws Throwable
	{
		try
		{
			Object result = joinPoint.proceed();
			tokenService.cacheResult(token, result);
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
			tokenService.deleteToken(token);
		}
	}
}