package io.github.quiethappiness.wrench.rate.limiter.domain.service;

import io.github.quiethappiness.wrench.design.framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.dynamic.config.center.types.annotations.DCCValue;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.RequestParameterEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.ResponseResultEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.factory.RateLimiterStrategyFactory;
import io.github.quiethappiness.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import javax.annotation.Resource;

/**
 * RateLimiterAOP
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 限流切面
 * @date 2025/9/12 15:40
 */
@Aspect
public class RateLimiterAOP extends AbstractRateLimiterAop
{
	@DCCValue("open")
	private String rateLimiterSwitch;
	
	@Resource
	private RateLimiterStrategyFactory defaultStrategyFactory;
	
	@Around("aopPoint() && @annotation(rateLimiterAccessInterceptor)")
	public Object doRouter(ProceedingJoinPoint jp, RateLimiterAccessInterceptor rateLimiterAccessInterceptor) throws Throwable
	{
		StrategyHandler<RequestParameterEntity, RateLimiterStrategyFactory.DynamicContext, ResponseResultEntity> strategyHandler = defaultStrategyFactory.strategyHandler();
		ResponseResultEntity result = strategyHandler.apply(
			RequestParameterEntity.builder()
				.rateLimiterSwitch(rateLimiterSwitch)
				.build(),
			RateLimiterStrategyFactory.DynamicContext.builder()
				.blacklist(blacklist)
				.loginRecord(loginRecord)
				.jp(jp)
				.rateLimiterAccessInterceptor(rateLimiterAccessInterceptor)
				.build());
		if (result.isDecideLimit())
		{
			return fallbackMethodResult(jp, rateLimiterAccessInterceptor.fallbackMethod());
		}
		// 返回结果
		return jp.proceed();
	}
}