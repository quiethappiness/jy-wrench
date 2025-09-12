package io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.node;

import com.google.common.cache.Cache;
import com.google.common.util.concurrent.RateLimiter;
import io.github.quiethappiness.wrench.design.framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.RequestParameterEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.ResponseResultEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.AbstractRateLimiterSupport;
import io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.factory.RateLimiterStrategyFactory;
import io.github.quiethappiness.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class PPSCheckNode extends AbstractRateLimiterSupport
{
	@Resource
	private EndNode endNode;
	
	@Override
	protected ResponseResultEntity doApply(RequestParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		log.info("【PPSCheckNode】：PPS 校验...");
		Cache<String, Long> blacklist = dynamicContext.getBlacklist();
		Cache<String, RateLimiter> loginRecord = dynamicContext.getLoginRecord();
		RateLimiterAccessInterceptor rateLimiterAccessInterceptor = dynamicContext.getRateLimiterAccessInterceptor();
		String keyAttr = dynamicContext.getKeyAttr();
		if (Boolean.TRUE.equals(rateLimiterAccessInterceptor.enabledPPS()))
		{
			RateLimiter rateLimiter = loginRecord.getIfPresent(keyAttr);
			if (null == rateLimiter)
			{
				rateLimiter = RateLimiter.create(rateLimiterAccessInterceptor.permitsPerSecond());
				loginRecord.put(keyAttr, rateLimiter);
			}
			// 限流拦截
			if (!rateLimiter.tryAcquire())
			{
				log.warn("【PPSCheckNode】:限流-获取通行证失败");
				if (rateLimiterAccessInterceptor.blacklistCount() != 0)
				{
					if (null == blacklist.getIfPresent(keyAttr))
					{
						blacklist.put(keyAttr, 1L);
					}
					else
					{
						blacklist.put(keyAttr, blacklist.getIfPresent(keyAttr) + 1L);
					}
				}
				log.error("【PPSCheckNode】:限流-超频次拦截：{}", keyAttr);
				dynamicContext.setDecideLimit(true);
			}
			else
			{
				log.info("【PPSCheckNode】:限流-获取通行证成功");
			}
		}
		return router(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<RequestParameterEntity, RateLimiterStrategyFactory.DynamicContext, ResponseResultEntity> get(RequestParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		return endNode;
	}
}