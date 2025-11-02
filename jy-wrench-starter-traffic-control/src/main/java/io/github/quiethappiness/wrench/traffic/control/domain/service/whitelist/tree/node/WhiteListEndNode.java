package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.node;

import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListVO;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.AbstractWhiteListSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.WhiteListStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhiteListEndNode extends AbstractWhiteListSupport
{
	@Override
	protected WhiteListVO.WhiteListResultEntity doApply(WhiteListVO.WhiteListParameterEntity requestParameter, WhiteListStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		final AttrValueResult valueResult = dynamicContext.getAttrValueResult();
		final boolean[] isInWhitelist = dynamicContext.getIsInWhitelist();
		InWhitListResult inWhitListResult = new InWhitListResult(valueResult.actualValue(), isInWhitelist[0]);
		return new WhiteListVO.WhiteListResultEntity(inWhitListResult);
	}
}