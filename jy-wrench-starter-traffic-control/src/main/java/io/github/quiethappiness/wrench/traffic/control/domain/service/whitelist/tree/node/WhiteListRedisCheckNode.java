package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.node;

import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.business.WhiteListService;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.AbstractWhiteListSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.WhiteListStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WhiteListRedisCheckNode extends AbstractWhiteListSupport
{
	
	private final WhiteListService whitelistService;
	
	private final WhiteListEndNode whiteListEndNode;
	
	@Override
	protected WhiteListResultEntity doApply(WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		final AttrValueResult attrValue = dynamicContext.getAttrValueResult();
		final String uri = dynamicContext.getUri();
		final boolean[] isInWhitelist = dynamicContext.getIsInWhitelist();
		try
		{
			log.warn("开始检查数据库白名单 id: {}", attrValue.userId());
			isInWhitelist[0] = whitelistService.checkWhitelistId(uri, attrValue.type(), attrValue.userId());
		}
		catch (Exception e)
		{
			log.error("Error checking whitelist for user: {}", attrValue.userId(), e);
		}
		return router(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<WhiteListParameterEntity, WhiteListStrategyFactory.DynamicContext, WhiteListResultEntity> get(WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		return whiteListEndNode;
	}
}
