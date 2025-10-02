package io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory;

import com.google.common.cache.Cache;
import com.google.common.util.concurrent.RateLimiter;
import io.github.quiethappiness.wrench.design.framework.tree.AbstractStrategyFactory;
import io.github.quiethappiness.wrench.design.framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterReturnResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.node.RateLimitRootNode;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.AccessRateLimiter;
import lombok.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimiterStrategyFactory extends AbstractStrategyFactory<RateLimiterParameterEntity, RateLimiterStrategyFactory.DynamicContext, RateLimiterReturnResultEntity>
{
	
	private final RateLimitRootNode rateLimitRootNode;
	
	public StrategyHandler<RateLimiterParameterEntity, DynamicContext, RateLimiterReturnResultEntity> strategyHandler()
	{
		return rateLimitRootNode;
	}
	
	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class DynamicContext
	{
		private boolean switchOpen;
		private boolean decideLimit;
		private ProceedingJoinPoint jp;
		private AccessRateLimiter accessRateLimiter;
		private String keyAttr;
		// 个人限频记录1分钟
		private Cache<String, RateLimiter> loginRecord;
		
		// 个人限频黑名单24h - 分布式业务场景，可以记录到 Redis 中
		private Cache<String, Long> blacklist;
	}
}