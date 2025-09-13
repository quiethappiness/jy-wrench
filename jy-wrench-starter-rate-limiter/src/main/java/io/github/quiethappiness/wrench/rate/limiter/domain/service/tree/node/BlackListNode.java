package io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.node;

import com.google.common.cache.Cache;
import io.github.quiethappiness.wrench.design.framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.RateLimiterParameterEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.RateLimiterReturnResultEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.AbstractRateLimiterSupport;
import io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.factory.RateLimiterStrategyFactory;
import io.github.quiethappiness.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Component("rateLimiterBlackListNode")
public class BlackListNode extends AbstractRateLimiterSupport
{
	@Resource
	private PPSCheckNode rateLimiterPPSCheckNode;
	
	@Resource
	private EndNode rateLimiterEndNode;
	
	/**
	 * 黑名单拦截节点处理方法
	 * 该方法用于检查请求是否命中黑名单规则：
	 * 1. 首先验证黑名单功能是否启用
	 * 2. 检查黑名单阈值配置是否有效
	 * 3. 查询缓存中对应keyAttr的访问次数
	 * 4. 如果访问次数超过设定阈值，则触发黑名单拦截
	 * 5. 最终将限流决策结果传递给下一个处理器
	 * @param requestParameter
	 * 	限流参数实体
	 * @param dynamicContext
	 * 	动态上下文环境
	 * @return 限流处理结果实体
	 * @throws Throwable
	 * 	处理过程中可能抛出的异常
	 */
	@Override
	protected RateLimiterReturnResultEntity doApply(RateLimiterParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		// 记录日志：开始处理黑名单拦截逻辑
		log.info("【BlackListNode】:黑名单拦截...");
		// 获取限流访问拦截器实例，用于获取相关配置信息
		RateLimiterAccessInterceptor rateLimiterAccessInterceptor = dynamicContext.getRateLimiterAccessInterceptor();
		// 获取当前请求对应的属性值（用于作为黑名单判断的KEY）
		String keyAttr = dynamicContext.getKeyAttr();
		// 获取黑名单缓存实例，用于查询访问次数统计
		Cache<String, Long> blacklist = dynamicContext.getBlacklist();
		// 条件判断：当满足以下所有条件时执行黑名单拦截逻辑
		// 1. 黑名单功能已启用
		// 2. 黑名单阈值大于0
		// 3. 在缓存中能找到该keyAttr对应的记录
		// 4. 该记录的访问次数超过了设定的黑名单阈值
		if (Boolean.TRUE.equals(rateLimiterAccessInterceptor.enableBlacklist())
			&& rateLimiterAccessInterceptor.blacklistCount() > 0
			&& null != blacklist.getIfPresent(keyAttr)
			&& Objects.requireNonNull(blacklist.getIfPresent(keyAttr)) > rateLimiterAccessInterceptor.blacklistCount())
		{
			// 记录错误日志：检测到黑名单拦截事件
			log.error("【BlackListNode】:进行限流-黑名单拦截(24h)：{}", keyAttr);
			// 设置限流决策标志为true，表示需要进行限流处理
			dynamicContext.setDecideLimit(true);
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
			return rateLimiterEndNode;
		}
		return rateLimiterPPSCheckNode;
	}
}