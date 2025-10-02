package io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.node;

import com.google.common.cache.Cache;
import io.github.quiethappiness.wrench.design.framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterReturnResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory.AbstractRateLimiterSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory.RateLimiterStrategyFactory;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.business.SlidingWindowRateLimiter;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcRateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.node.RateLimitBlackListNode.blackListCheck;

@Component("RateLimitSWRNode")
@Slf4j
@RequiredArgsConstructor
public class RateLimitSWRNode extends AbstractRateLimiterSupport
{
	private final RateLimitEndNode RateLimitEndNode;
	private final SlidingWindowRateLimiter slidingWindowRateLimiter;
	@Override
	protected RateLimiterReturnResultEntity doApply(RateLimiterParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		// 记录日志：开始执行PPS校验逻辑
		log.info("【RateLimitSWRNode】：SWR 校验...");
		// 获取黑名单缓存实例，用于记录超频请求
		Cache<String, Long> blacklist = dynamicContext.getBlacklist();
		// 获取限流访问拦截器实例，用于获取相关配置信息
		TcRateLimiter tcRateLimiter = dynamicContext.getTcRateLimiter();
		String keyAttr = dynamicContext.getKeyAttr();
		long windowSizeMs = tcRateLimiter.windowSizeMs();
		long maxRequests = tcRateLimiter.maxRequests();
		if(!slidingWindowRateLimiter.tryAcquire(keyAttr, windowSizeMs, maxRequests))
		{
			// 记录警告日志：获取通行证失败，触发限流
			log.warn("【RateLimitSWRNode】:限流-获取通行证失败");
			// 检查是否启用了黑名单机制且设置了阈值
			blackListCheck(tcRateLimiter.mode(),  blacklist, keyAttr);
			// 记录错误日志：检测到超频次拦截事件
			log.error("【RateLimitSWRNode】:限流-超频次拦截：{}", keyAttr);
			// 设置限流决策标志为true，表示需要进行限流处理
			dynamicContext.setDecideLimit(true);
		}
		else
		{
			// 记录信息日志：成功获取通行证，允许通过
			log.info("【RateLimitSWRNode】:限流-获取通行证成功");
		}
		// 调用路由方法，继续向下个节点传递处理结果
		return router(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<RateLimiterParameterEntity, RateLimiterStrategyFactory.DynamicContext, RateLimiterReturnResultEntity> get(RateLimiterParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		// 决定限流
		if (dynamicContext.isDecideLimit())
		{
			return RateLimitEndNode;
		}
		return RateLimitEndNode;
	}
}
