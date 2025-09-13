package io.github.quiethappiness.wrench.rate.limiter.domain.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Pointcut;

/**
 * IRateLimiterAOP
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 限流切面接口
 * @date 2025/9/12 15:52
 */
public interface IRateLimiterAOP
{
	@Pointcut("@annotation(io.github.quiethappiness.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor)")
	default void aopPoint() {
	}
	
	Object fallbackMethodResult(JoinPoint jp, String fallbackMethod) throws Exception;
	
}