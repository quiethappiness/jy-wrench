package io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterReturnResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.service.IRateLimiterAOP;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory.RateLimiterStrategyFactory;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcRateLimiter;
import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.concurrent.TimeUnit;

/**
 * AbstractRateLimiterAop
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 抽象实现
 * @date 2025/9/12 15:54
 */
public abstract class AbstractRateLimiterAop implements IRateLimiterAOP
{
	// 个人限频记录1分钟
	protected final Cache<String, RateLimiter> loginRecord = CacheBuilder.newBuilder()
		.expireAfterWrite(1, TimeUnit.MINUTES)
		.build();
	
	// 个人限频黑名单24h - 分布式业务场景，可以记录到 Redis 中
	protected final Cache<String, Long> blacklist = CacheBuilder.newBuilder()
		.expireAfterWrite(24, TimeUnit.HOURS)
		.build();
	
	@Resource
	protected RateLimiterStrategyFactory rateLimiterStrategyFactory;
	
	protected RateLimiterReturnResultEntity doAccess(final ProceedingJoinPoint jp, final TcRateLimiter tcRateLimiter, String rateLimiterSwitch) throws Throwable
	{
		StrategyHandler<RateLimiterParameterEntity, RateLimiterStrategyFactory.DynamicContext, RateLimiterReturnResultEntity> strategyHandler = rateLimiterStrategyFactory.strategyHandler();
		return strategyHandler.apply(
			RateLimiterParameterEntity.builder()
				.rateLimiterSwitch(rateLimiterSwitch)
				.blacklist(blacklist)
				.loginRecord(loginRecord)
				.jp(jp)
				.tcRateLimiter(tcRateLimiter)
				.build(),
			RateLimiterStrategyFactory.DynamicContext.builder()
				.build());
	}
}