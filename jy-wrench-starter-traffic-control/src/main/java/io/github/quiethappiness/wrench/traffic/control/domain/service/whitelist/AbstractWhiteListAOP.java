package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist;

import io.github.quiethappiness.wrench.design.framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.config.property.WhiteListProperties;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.service.IWhiteListAOP;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.WhiteListStrategyFactory;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.WhiteListChecker;
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
	
	protected WhiteListResultEntity doCheck(ProceedingJoinPoint jp, WhiteListChecker whiteListChecker) throws Throwable
	{
		StrategyHandler<WhiteListParameterEntity, WhiteListStrategyFactory.DynamicContext, WhiteListResultEntity> strategyHandler = whiteListStrategyFactory.strategyHandler();
		return strategyHandler.apply(new WhiteListParameterEntity(whiteListChecker, jp, whiteListProperties), new WhiteListStrategyFactory.DynamicContext());
	}
	
}
