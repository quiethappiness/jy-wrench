package io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent;

import io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.strategy.IdempotentStrategy;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcIdempotent;
import io.github.quiethappiness.wrench.traffic.control.types.exception.IdempotentException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

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
	@Resource
	private Map<String, IdempotentStrategy> idempotentStrategyMap;
	
	@Around("accessIdempotentPointcut() && @annotation(idempotent)")
	public Object aroundMethod(ProceedingJoinPoint joinPoint, TcIdempotent idempotent) throws Throwable
	{
		// 1. 获取令牌
		String token = idempotentToken.getTokenFromRequest(idempotent.tokenHeader());
		if (token == null)
		{
			throw new IdempotentException("缺少幂等令牌");
		}
		// 2. 根据级别进行处理
		return processByLevel(joinPoint, idempotent, token);
	}
	
	private Object processByLevel(ProceedingJoinPoint joinPoint, TcIdempotent idempotent, String token) throws Throwable
	{
		IdempotentStrategy.IdempotentStrategyEnum strategyEnum = IdempotentStrategy.IdempotentStrategyEnum.getByMap(idempotent.level());
		IdempotentStrategy strategy = idempotentStrategyMap.get(strategyEnum.getBeanName());
		// 令牌检查
		Object previousResult = strategy.tokenCheck(joinPoint, idempotent, token);
		if (previousResult != null)
		{
			return previousResult;
		}
		// 频率检查
		strategy.frequencyCheck(joinPoint, idempotent, token);
		// 相似请求检查
		strategy.similarCheck(joinPoint, token);
		return strategy.execJoinPoint(joinPoint, token);
	}
}