package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory;

import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListResultEntity;
import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;
import io.github.quiethappiness.wrench.util.design_framework.tree.AbstractMultiThreadStrategyRouter;
import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractWhiteListSupport extends AbstractMultiThreadStrategyRouter<WhiteListParameterEntity, WhiteListStrategyFactory.DynamicContext, WhiteListResultEntity>
{
	@Override
	protected void multiThread(WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
	}
	
	@Override
	protected WhiteListResultEntity doApply(WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		return null;
	}
	
	@Override
	public StrategyHandler<WhiteListParameterEntity, WhiteListStrategyFactory.DynamicContext, WhiteListResultEntity> get(WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		return defaultStrategyHandler;
	}
	
	@Builder
	public record AttrValueResult(WhiteListType type, String userId)
	{
	}
	
	@Builder
	public record InWhitListResult(String userId, boolean isInWhitelist)
	{
	}
}
