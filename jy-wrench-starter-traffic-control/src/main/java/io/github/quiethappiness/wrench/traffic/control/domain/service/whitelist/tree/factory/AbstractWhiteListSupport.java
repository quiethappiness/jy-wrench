package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory;

import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListVO;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcWhiteList;
import io.github.quiethappiness.wrench.util.design_framework.tree.AbstractMultiThreadStrategyRouter;
import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractWhiteListSupport extends AbstractMultiThreadStrategyRouter<WhiteListVO.WhiteListParameterEntity, WhiteListStrategyFactory.DynamicContext, WhiteListVO.WhiteListResultEntity>
{
	@Override
	protected void multiThread(WhiteListVO.WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
	}
	
	@Override
	protected WhiteListVO.WhiteListResultEntity doApply(WhiteListVO.WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		return null;
	}
	
	@Override
	public StrategyHandler<WhiteListVO.WhiteListParameterEntity, WhiteListStrategyFactory.DynamicContext, WhiteListVO.WhiteListResultEntity> get(WhiteListVO.WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		return defaultStrategyHandler;
	}
	
	@Builder
	public record AttrValueResult(TcWhiteList.WhiteListType type, String actualValue)
	{
	}
	
	@Builder
	public record InWhitListResult(String userId, boolean isInWhitelist)
	{
	}
}