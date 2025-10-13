package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.node;

import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.AbstractWhiteListSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.WhiteListStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhiteListEndNode extends AbstractWhiteListSupport
{
	@Override
	protected WhiteListResultEntity doApply(WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		final AttrValueResult valueResult = dynamicContext.getAttrValueResult();
		final boolean[] isInWhitelist = dynamicContext.getIsInWhitelist();
		InWhitListResult inWhitListResult = new InWhitListResult(valueResult.userId(), isInWhitelist[0]);
		return new WhiteListResultEntity(inWhitListResult);
	}
}
