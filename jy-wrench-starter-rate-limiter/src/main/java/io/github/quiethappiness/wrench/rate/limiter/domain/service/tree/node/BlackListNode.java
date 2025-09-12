package io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.node;

import com.google.common.cache.Cache;
import io.github.quiethappiness.wrench.design.framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.RequestParameterEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.ResponseResultEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.AbstractRateLimiterSupport;
import io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.factory.RateLimiterStrategyFactory;
import io.github.quiethappiness.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Component
public class BlackListNode extends AbstractRateLimiterSupport
{
	@Resource
	private PPSCheckNode ppsCheckNode;
	
	@Resource
	private EndNode endNode;
	@Override
	protected ResponseResultEntity doApply(RequestParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		log.info("【BlackListNode】:黑名单拦截...");
		RateLimiterAccessInterceptor rateLimiterAccessInterceptor = dynamicContext.getRateLimiterAccessInterceptor();
		String keyAttr = dynamicContext.getKeyAttr();
		Cache<String, Long> blacklist = dynamicContext.getBlacklist();
		if (Boolean.TRUE.equals(rateLimiterAccessInterceptor.enableBlacklist())
			&& rateLimiterAccessInterceptor.blacklistCount() > 0
			&& null != blacklist.getIfPresent(keyAttr)
			&& Objects.requireNonNull(blacklist.getIfPresent(keyAttr)) > rateLimiterAccessInterceptor.blacklistCount())
		{
			log.error("【BlackListNode】:进行限流-黑名单拦截(24h)：{}", keyAttr);
			dynamicContext.setDecideLimit(true);
		}
		return router(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<RequestParameterEntity, RateLimiterStrategyFactory.DynamicContext, ResponseResultEntity> get(RequestParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		// 决定限流
		if (dynamicContext.isDecideLimit())
		{
			return endNode;
		}
		return ppsCheckNode;
	}
}