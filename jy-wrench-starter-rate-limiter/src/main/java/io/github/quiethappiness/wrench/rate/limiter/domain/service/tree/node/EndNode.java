package io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.node;

import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.RequestParameterEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.ResponseResultEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.AbstractRateLimiterSupport;
import io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.factory.RateLimiterStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("rateLimiterEndNode")
public class EndNode extends AbstractRateLimiterSupport
{
    @Override
    protected ResponseResultEntity doApply(RequestParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Throwable
    {
        log.warn("【结束节点】:是否限流-->{}", dynamicContext.isDecideLimit());
        return ResponseResultEntity.builder().decideLimit(dynamicContext.isDecideLimit()).build();
    }
}