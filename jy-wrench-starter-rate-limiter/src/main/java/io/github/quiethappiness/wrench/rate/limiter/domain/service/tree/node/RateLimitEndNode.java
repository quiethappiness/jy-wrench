package io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.node;

import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.RateLimiterParameterEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.RateLimiterReturnResultEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.factory.AbstractRateLimiterSupport;
import io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.factory.RateLimiterStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("RateLimitEndNode")
public class RateLimitEndNode extends AbstractRateLimiterSupport
{
	/**
	 * 限流处理流程结束节点
	 * 该方法作为限流处理链的终点，负责：
	 * 1. 记录最终的限流决策结果
	 * 2. 构建并返回限流处理结果实体
	 * 3. 输出处理完成的日志信息
	 * @param requestParameter
	 * 	限流参数实体
	 * @param dynamicContext
	 * 	动态上下文环境，包含完整的处理状态信息
	 * @return 限流处理结果实体，包含最终的限流决策标志
	 * @throws Throwable
	 * 	处理过程中可能抛出的异常
	 */
	@Override
	protected RateLimiterReturnResultEntity doApply(RateLimiterParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		// 记录日志：输出最终的限流决策结果
		log.warn("【结束节点】:是否限流-->{}", dynamicContext.isDecideLimit());
		// 构建并返回限流处理结果实体
		// 结果中包含最终的限流决策标志位，供上层调用者使用
		return RateLimiterReturnResultEntity.builder()
			.decideLimit(dynamicContext.isDecideLimit())
			.build();
	}
}