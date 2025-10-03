package io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory;

import io.github.quiethappiness.wrench.design.framework.tree.AbstractStrategyFactory;
import io.github.quiethappiness.wrench.design.framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterReturnResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.node.RateLimitRootNode;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcRateLimiter;
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

		private String keyAttr;

	}
}