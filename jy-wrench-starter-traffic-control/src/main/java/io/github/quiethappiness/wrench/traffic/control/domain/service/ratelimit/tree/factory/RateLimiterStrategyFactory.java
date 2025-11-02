package io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory;

import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterVO;
import io.github.quiethappiness.wrench.util.design_framework.tree.AbstractStrategyFactory;
import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.node.RateLimitRootNode;
import lombok.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimiterStrategyFactory extends AbstractStrategyFactory<RateLimiterVO.ParameterEntity, RateLimiterStrategyFactory.DynamicContext, RateLimiterVO.ReturnResultEntity>
{
	
	private final RateLimitRootNode rateLimitRootNode;
	
	public StrategyHandler<RateLimiterVO.ParameterEntity, DynamicContext, RateLimiterVO.ReturnResultEntity> strategyHandler()
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