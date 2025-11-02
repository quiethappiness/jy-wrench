package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist;

import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListVO;
import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.config.property.WhiteListProperties;
import io.github.quiethappiness.wrench.traffic.control.domain.service.IWhiteListAOP;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.WhiteListStrategyFactory;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcWhiteList;
import org.aspectj.lang.ProceedingJoinPoint;

public abstract class AbstractWhiteListAOP implements IWhiteListAOP
{
	protected final WhiteListProperties whiteListProperties;
	
	protected final WhiteListStrategyFactory whiteListStrategyFactory;
	
	protected AbstractWhiteListAOP(WhiteListProperties whiteListProperties, WhiteListStrategyFactory whiteListStrategyFactory)
	{
		this.whiteListProperties = whiteListProperties;
		this.whiteListStrategyFactory = whiteListStrategyFactory;
	}
	
	protected WhiteListVO.WhiteListResultEntity doTreeCheck(ProceedingJoinPoint jp, TcWhiteList tcWhiteList) throws Throwable
	{
		StrategyHandler<WhiteListVO.WhiteListParameterEntity, WhiteListStrategyFactory.DynamicContext, WhiteListVO.WhiteListResultEntity> strategyHandler = whiteListStrategyFactory.strategyHandler();
		return strategyHandler.apply(new WhiteListVO.WhiteListParameterEntity(tcWhiteList, jp, whiteListProperties), new WhiteListStrategyFactory.DynamicContext());
	}
	
}