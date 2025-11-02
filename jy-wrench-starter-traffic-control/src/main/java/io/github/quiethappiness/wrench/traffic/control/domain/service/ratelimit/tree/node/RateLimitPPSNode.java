package io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.node;

import com.google.common.cache.Cache;
import com.google.common.util.concurrent.RateLimiter;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterVO;
import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory.AbstractRateLimiterSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory.RateLimiterStrategyFactory;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcRateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.node.RateLimitBlackListNode.blackListCheck;

@Slf4j
@Component("RateLimitPPSNode")
public class RateLimitPPSNode extends AbstractRateLimiterSupport
{
	private final RateLimitEndNode RateLimitEndNode;
	
	private final TimeLimiter timeLimiter;
	
	public RateLimitPPSNode(
		RateLimitEndNode RateLimitEndNode,
		ExecutorService executor)
	{
		this.RateLimitEndNode = RateLimitEndNode;
		this.timeLimiter = SimpleTimeLimiter.create(executor);
	}
	
	/**
	 * PPS（每秒请求数）校验节点处理方法
	 * 该方法用于实现基于令牌桶算法的限流控制：
	 * 1. 首先检查PPS限流功能是否启用
	 * 2. 根据请求key获取或创建对应的限流器实例
	 * 3. 尝试获取令牌，若失败则进行限流处理
	 * 4. 若启用了黑名单机制，在超频时将请求加入黑名单
	 * 5. 记录相应的日志信息并返回处理结果
	 * @param requestParameter
	 * 	限流参数实体
	 * @param dynamicContext
	 * 	动态上下文环境
	 * @return 限流处理结果实体
	 * @throws Throwable
	 * 	处理过程中可能抛出的异常
	 */
	@Override
	protected RateLimiterVO.ReturnResultEntity doApply(RateLimiterVO.ParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		// 记录日志：开始执行PPS校验逻辑
		log.info("【RateLimitPPSNode】：PPS 校验...");
		// 获取黑名单缓存实例，用于记录超频请求
		final Cache<String, Long> blacklist = requestParameter.blacklist();
		// 获取登录记录缓存实例，存储各请求的限流器
		final Cache<String, RateLimiter> loginRecord = requestParameter.loginRecord();
		// 获取限流访问拦截器实例，用于获取相关配置信息
		final TcRateLimiter tcRateLimiter = requestParameter.tcRateLimiter();
		double permitsPerSecond = tcRateLimiter.permitsPerSecond();
		long warmupPeriod = tcRateLimiter.warmupPeriod();
		TimeUnit unit = tcRateLimiter.unit();
		// 获取当前请求对应的属性值（用于作为限流判断的KEY）
		String keyAttr = dynamicContext.getKeyAttr();
		// 条件判断：只有当PPS限流功能启用时才执行后续逻辑
		// 从缓存中尝试获取对应key的限流器实例
		// 获取限流 -> Guava 缓存1分钟
		// 为每个用户创建一个限流器实例，在1分钟内，限制访问次数
		RateLimiter rateLimiter = loginRecord.getIfPresent(keyAttr);
		// 如果缓存中不存在对应的限流器，则创建一个新的
		if (null == rateLimiter)
		{
			// 使用配置的每秒许可数创建新的令牌桶限流器
			rateLimiter = timeLimiter.callWithTimeout(
				() -> RateLimiter.create(permitsPerSecond, warmupPeriod, unit),
				5, TimeUnit.SECONDS
			);
			// 将新创建的限流器放入缓存中供下次使用
			loginRecord.put(keyAttr, rateLimiter);
		}
		// 尝试获取令牌（即进行限流判断）
		// 如果无法获取到令牌，表示请求频率过高，需要进行限流处理
		if (!rateLimiter.tryAcquire())
		{
			// 记录警告日志：获取通行证失败，触发限流
			log.warn("【RateLimitPPSNode】:限流-获取通行证失败");
			// 检查是否启用了黑名单机制且设置了阈值
			blackListCheck(tcRateLimiter.mode(), blacklist, keyAttr);
			// 记录错误日志：检测到超频次拦截事件
			log.error("【RateLimitPPSNode】:限流-超频次拦截：{}", keyAttr);
			// 设置限流决策标志为true，表示需要进行限流处理
			dynamicContext.setDecideLimit(true);
		}
		else
		{
			// 记录信息日志：成功获取通行证，允许通过
			log.info("【RateLimitPPSNode】:限流-获取通行证成功");
		}
		// 调用路由方法，继续向下个节点传递处理结果
		return router(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<RateLimiterVO.ParameterEntity, RateLimiterStrategyFactory.DynamicContext, RateLimiterVO.ReturnResultEntity> get(RateLimiterVO.ParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		return RateLimitEndNode;
	}
}