package io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.node;

import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterReturnResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory.AbstractRateLimiterSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory.RateLimiterStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component("RateLimitRootNode")
@RequiredArgsConstructor
public class RateLimitRootNode extends AbstractRateLimiterSupport
{
	private final RateLimitSwitchNode RateLimitSwitchNode;
	
	private final RateLimitEndNode RateLimitEndNode;
	
	@Override
	protected RateLimiterReturnResultEntity doApply(RateLimiterParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		log.info("【根节点】限流开关:{}", requestParameter.getRateLimiterSwitch());
		// 0. 限流开关【open 开启、close, '' 关闭】关闭后，不会走限流策略
		String rateLimiterSwitch = requestParameter.getRateLimiterSwitch();
		dynamicContext.setSwitchOpen(!StringUtils.isBlank(rateLimiterSwitch) && !"close".equals(rateLimiterSwitch));
		return router(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<RateLimiterParameterEntity, RateLimiterStrategyFactory.DynamicContext, RateLimiterReturnResultEntity> get(RateLimiterParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		// 开关确定关闭，即不开启限流，则直接返回结果
		if (!dynamicContext.isSwitchOpen())
		{
			return RateLimitEndNode;
		}
		return RateLimitSwitchNode;
	}
}