package io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.factory;

import com.google.common.cache.Cache;
import com.google.common.util.concurrent.RateLimiter;
import io.github.quiethappiness.wrench.design.framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.RequestParameterEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.ResponseResultEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.node.RootNode;
import io.github.quiethappiness.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterStrategyFactory
{
	
	private final RootNode rateLimiterRootNode;
	
	public RateLimiterStrategyFactory(RootNode rateLimiterRootNode)
	{
		this.rateLimiterRootNode = rateLimiterRootNode;
	}
	
	public StrategyHandler<RequestParameterEntity, DynamicContext, ResponseResultEntity> strategyHandler()
	{
		return rateLimiterRootNode;
	}
	
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class DynamicContext
	{
		private boolean decideLimit;
		private ProceedingJoinPoint jp;
		private RateLimiterAccessInterceptor rateLimiterAccessInterceptor;
		private String keyAttr;
		// 个人限频记录1分钟
		private Cache<String, RateLimiter> loginRecord;
		
		// 个人限频黑名单24h - 分布式业务场景，可以记录到 Redis 中
		private Cache<String, Long> blacklist;
	}
}