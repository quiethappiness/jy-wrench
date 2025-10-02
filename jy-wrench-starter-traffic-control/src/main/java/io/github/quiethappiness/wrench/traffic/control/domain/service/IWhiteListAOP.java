package io.github.quiethappiness.wrench.traffic.control.domain.service;

import org.aspectj.lang.annotation.Pointcut;

public interface IWhiteListAOP
{
	@Pointcut("@annotation(io.github.quiethappiness.wrench.traffic.control.types.annotations.TcWhiteList)")
	default void whiteListCheckerPointCut()
	{
	}
	
	@Pointcut("whiteListCheckerPointCut() && io.github.quiethappiness.wrench.traffic.control.domain.service.IRateLimiterAOP.accessRateLimiterPointcut() &&  @within(org.springframework.web.bind.annotation.RestController) ")
	default void whiteListCheckerWithRateLimiter()
	{
	}
	
	@Pointcut("whiteListCheckerPointCut() && !io.github.quiethappiness.wrench.traffic.control.domain.service.IRateLimiterAOP.accessRateLimiterPointcut() && @within(org.springframework.web.bind.annotation.RestController) ")
	default void whiteListCheckerWithoutRateLimiter()
	{
	}
}
