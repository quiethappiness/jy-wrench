package io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent;

import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcIdempotent;
import io.github.quiethappiness.wrench.traffic.control.types.exception.IdempotentException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * RateLimiterAOP
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 限流切面
 * @date 2025/9/12 15:40
 */
@Aspect
@Component
@Order(-1)
@Slf4j
public class IdempotentAOP extends AbstractIdempotentAOP
{
	
	@Around("accessIdempotentPointcut() && @annotation(idempotent)")
	public Object aroundMethod(ProceedingJoinPoint joinPoint, TcIdempotent idempotent) throws Throwable
	{
		// 1. 获取令牌
		String token = tokenService.getTokenFromRequest(idempotent.tokenHeader());
		if (token == null)
		{
			throw new IdempotentException("缺少幂等令牌");
		}
		// 2. 根据级别进行处理
		return processByLevel(joinPoint, idempotent, token);
	}
	
	private Object processByLevel(ProceedingJoinPoint joinPoint, TcIdempotent idempotent, String token) throws Throwable
	{
		return switch (idempotent.level())
		{
			case FAST -> processFastLevel(joinPoint, idempotent, token);
			case STRICT -> processStrictLevel(joinPoint, idempotent, token);
			default -> processNormalLevel(joinPoint, idempotent, token);
		};
	}
	
	/**
	 * 快速级别处理：只检查令牌是否已使用
	 */
	private Object processFastLevel(ProceedingJoinPoint joinPoint, TcIdempotent idempotent, String token) throws Throwable
	{
		// 检查并标记令牌
		Object previousResult = tokenCheck(joinPoint, idempotent, token);
		if (previousResult != null)
		{
			return previousResult;
		}
		return execJoinPoint(joinPoint,  token);
	}
	
	/**
	 * 常规级别处理：增加基本的频率检查
	 */
	private Object processNormalLevel(ProceedingJoinPoint joinPoint, TcIdempotent idempotent, String token) throws Throwable
	{
		// 先进行令牌检查
		Object previousResult = tokenCheck(joinPoint, idempotent, token);
		if (previousResult != null)
		{
			return previousResult;
		}
		// 额外的频率检查（可选）
		frequencyCheck(joinPoint,idempotent, token);
		return execJoinPoint(joinPoint,  token);
	}
	
	/**
	 * 严格级别处理：完整的校验流程
	 */
	private Object processStrictLevel(ProceedingJoinPoint joinPoint, TcIdempotent idempotent, String token) throws Throwable
	{
		// 令牌检查
		Object previousResult = tokenCheck(joinPoint, idempotent, token);
		if (previousResult != null)
		{
			return previousResult;
		}
		// 频率检查
		frequencyCheck(joinPoint,idempotent, token);
		// 相似请求检查
		similarCheck(joinPoint,  token);
		return execJoinPoint(joinPoint,  token);
	}
}