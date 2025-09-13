package io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.node;

import io.github.quiethappiness.wrench.design.framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.RequestParameterEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.ResponseResultEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.AbstractRateLimiterSupport;
import io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.factory.RateLimiterStrategyFactory;
import io.github.quiethappiness.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("rateLimiterSwitchNode")
public class SwitchNode extends AbstractRateLimiterSupport
{
	
	@Resource
	private BlackListNode rateLimiterBlackListNode;
	
	@Override
	protected ResponseResultEntity doApply(RequestParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		log.info("【SwitchNode】:限流-开始");
		dynamicContext.setDecideLimit(false);
		RateLimiterAccessInterceptor rateLimiterAccessInterceptor = dynamicContext.getRateLimiterAccessInterceptor();
		ProceedingJoinPoint jp = dynamicContext.getJp();
		// 1. 获取限流字段
		String key = rateLimiterAccessInterceptor.key();
		if (StringUtils.isBlank(key))
		{
			throw new RuntimeException("annotation RateLimiter uId is null！");
		}
		// 获取拦截字段
		String keyAttr = getAttrValue(key, jp.getArgs());
		log.info("【SwitchNode】:限流-获取aop attr {}", keyAttr);
		dynamicContext.setKeyAttr(keyAttr);
		return router(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<RequestParameterEntity, RateLimiterStrategyFactory.DynamicContext, ResponseResultEntity> get(RequestParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		return rateLimiterBlackListNode;
	}
}