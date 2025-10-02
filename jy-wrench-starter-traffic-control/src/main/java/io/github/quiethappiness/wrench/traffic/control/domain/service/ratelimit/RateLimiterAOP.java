package io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit;

import io.github.quiethappiness.wrench.design.framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.dynamic.config.center.types.annotations.DCCValue;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterReturnResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.valobj.TrafficControlContext;
import io.github.quiethappiness.wrench.traffic.control.domain.service.IRateLimiterAOP;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory.RateLimiterStrategyFactory;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcRateLimiter;
import lombok.RequiredArgsConstructor;
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
@Order(2)
@RequiredArgsConstructor
public class RateLimiterAOP extends AbstractRateLimiterAop
{
	@DCCValue("open")
	private String rateLimiterSwitch;
	
	private final RateLimiterStrategyFactory rateLimiterStrategyFactory;
	
	/**
	 * 限流拦截器的核心处理方法，用于执行限流策略并决定是否允许请求继续执行
	 *
	 * @param jp 切点对象，包含被拦截方法的执行信息
	 * @param tcRateLimiter 限流注解对象，包含限流配置信息
	 * @return 如果未被限流则返回原方法执行结果，如果被限流则返回降级方法的结果
	 * @throws Throwable 方法执行异常时抛出
	 */
	@Around("accessRateLimiterPointcut() && @annotation(tcRateLimiter)")
	public Object doAccessRateLimiter(ProceedingJoinPoint jp, TcRateLimiter tcRateLimiter) throws Throwable
	{
		// 检查是否来自白名单AOP的标记
		if (TrafficControlContext.isInWhiteList()) {
			// 用户不在白名单中，需要检查限流
			return jp.proceed();
		}
		// 构造限流参数和上下文信息，执行限流策略判断
		StrategyHandler<RateLimiterParameterEntity, RateLimiterStrategyFactory.DynamicContext, RateLimiterReturnResultEntity> strategyHandler = rateLimiterStrategyFactory.strategyHandler();
		RateLimiterReturnResultEntity result = strategyHandler.apply(
			RateLimiterParameterEntity.builder()
				.rateLimiterSwitch(rateLimiterSwitch)
				.build(),
			RateLimiterStrategyFactory.DynamicContext.builder()
				.blacklist(blacklist)
				.loginRecord(loginRecord)
				.jp(jp)
				.tcRateLimiter(tcRateLimiter)
				.build());
		
		// 如果策略决定进行限流，则执行降级方法并返回结果
		if (result.isDecideLimit())
		{
			return IRateLimiterAOP.fallbackMethodResult(jp, tcRateLimiter.fallbackMethod());
		}
		// 返回结果
		return jp.proceed();
	}

}