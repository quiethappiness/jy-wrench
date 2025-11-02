package io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory;

import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterVO;
import io.github.quiethappiness.wrench.util.design_framework.tree.AbstractMultiThreadStrategyRouter;

public abstract class AbstractRateLimiterSupport extends AbstractMultiThreadStrategyRouter<RateLimiterVO.ParameterEntity, RateLimiterStrategyFactory.DynamicContext, RateLimiterVO.ReturnResultEntity>
{
	
	@Override
	protected void multiThread(RateLimiterVO.ParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		// 缺省的方法
	}
	

}