package io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory;

import io.github.quiethappiness.wrench.design.framework.tree.AbstractMultiThreadStrategyRouter;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterReturnResultEntity;

public abstract class AbstractRateLimiterSupport extends AbstractMultiThreadStrategyRouter<RateLimiterParameterEntity, RateLimiterStrategyFactory.DynamicContext, RateLimiterReturnResultEntity>
{
	
	@Override
	protected void multiThread(RateLimiterParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		// 缺省的方法
	}
	

}