package io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.node;

import com.google.common.cache.Cache;
import io.github.quiethappiness.wrench.design.framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterReturnResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory.AbstractRateLimiterSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory.RateLimiterStrategyFactory;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcRateLimiter;
import io.github.quiethappiness.wrench.traffic.control.types.enumvo.TrafficMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component("RateLimitBlackListNode")
@RequiredArgsConstructor
public class RateLimitBlackListNode extends AbstractRateLimiterSupport
{
	private final RateLimitPPSNode RateLimitPPSNode;
	
	private final RateLimitEndNode RateLimitEndNode;
	private final RateLimitSWRNode RateLimitSWRNode;
	
	/**
	 * 黑名单拦截节点处理方法
	 * 该方法用于检查请求是否命中黑名单规则：
	 * 1. 首先验证黑名单功能是否启用
	 * 2. 检查黑名单阈值配置是否有效
	 * 3. 查询缓存中对应keyAttr的访问次数
	 * 4. 如果访问次数超过设定阈值，则触发黑名单拦截
	 * 5. 最终将限流决策结果传递给下一个处理器
	 *
	 * @param requestParameter
	 * 	限流参数实体
	 * @param dynamicContext
	 * 	动态上下文环境
	 *
	 * @return 限流处理结果实体
	 *
	 * @throws Throwable
	 * 	处理过程中可能抛出的异常
	 */
	@Override
	protected RateLimiterReturnResultEntity doApply(RateLimiterParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		// 记录日志：开始处理黑名单拦截逻辑
		log.info("【RateLimitBlackListNode】:黑名单拦截...");
		// 获取限流访问拦截器实例，用于获取相关配置信息
		TcRateLimiter tcRateLimiter = dynamicContext.getTcRateLimiter();
		// 获取当前请求对应的属性值（用于作为黑名单判断的KEY）
		String keyAttr = dynamicContext.getKeyAttr();
		// 获取黑名单缓存实例，用于查询访问次数统计
		Cache<String, Long> blacklist = dynamicContext.getBlacklist();
		// 条件判断：当满足以下所有条件时执行黑名单拦截逻辑
		// 1. 黑名单功能已启用
		// 2. 黑名单阈值大于0
		// 3. 在缓存中能找到该keyAttr对应的记录
		// 4. 该记录的访问次数超过了设定的黑名单阈值
		Long ifPresent = blacklist.getIfPresent(keyAttr);
		log.warn("【RateLimitBlackListNode】:黑名单KEY为：{}, 黑名单访问次数为：{}", keyAttr, ifPresent);
		double blacklistCount = tcRateLimiter.blacklistCount();
		log.warn("【RateLimitBlackListNode】:黑名单阈值为：{}", blacklistCount);
		if (blacklistCount > 0
			&& null != ifPresent
			&& Objects.requireNonNull(ifPresent) > blacklistCount)
		{
			// 记录错误日志：检测到黑名单拦截事件
			log.error("【RateLimitBlackListNode】:进行限流-黑名单拦截(24h)：{}", keyAttr);
			// 设置限流决策标志为true，表示需要进行限流处理
			dynamicContext.setDecideLimit(true);
		}
		// 调用路由方法，继续向下个节点传递处理结果
		return router(requestParameter, dynamicContext);
	}
	
	public static void blackListCheck(TrafficMode limitMode, Cache<String, Long> blacklist, String keyAttr)
	{
		if (limitMode.equals(TrafficMode.PPS_BLACKLIST)|| limitMode.equals(TrafficMode.SWR_BLACKLIST))
		{
			log.warn("【RateLimitBlackListNode】:正在加入黑名单，黑名单KEY为：{}", keyAttr);
			// 查询当前key在黑名单中的计数
			if (null == blacklist.getIfPresent(keyAttr))
			{
				// 如果之前没有记录，则初始化为1
				blacklist.put(keyAttr, 1L);
			}
			else
			{
				// 如果已有记录，则增加计数
				blacklist.put(keyAttr, Objects.requireNonNull(blacklist.getIfPresent(keyAttr)) + 1L);
			}
		}
	}
	
	@Override
	public StrategyHandler<RateLimiterParameterEntity, RateLimiterStrategyFactory.DynamicContext, RateLimiterReturnResultEntity> get(RateLimiterParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		TcRateLimiter interceptor = dynamicContext.getTcRateLimiter();
		TrafficMode mode = interceptor.mode();
		// 决定限流
		if (dynamicContext.isDecideLimit())
		{
			return RateLimitEndNode;
		}
		else if (mode.equals(TrafficMode.PPS_BLACKLIST))
		{
			log.warn("【RateLimitSwitchNode】:限流-进入PPS节点");
			return RateLimitPPSNode;
		}
		else if (mode.equals(TrafficMode.SWR_BLACKLIST))
		{
			log.warn("【RateLimitSwitchNode】:限流-进入SWR节点");
			return RateLimitSWRNode;
		}
		return defaultStrategyHandler;
	}
}