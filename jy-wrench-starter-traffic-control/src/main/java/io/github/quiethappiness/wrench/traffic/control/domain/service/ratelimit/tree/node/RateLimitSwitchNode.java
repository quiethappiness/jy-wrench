package io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.node;

import io.github.quiethappiness.wrench.util.design_framework.tree.StrategyHandler;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterParameterEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.RateLimiterReturnResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory.AbstractRateLimiterSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.factory.RateLimiterStrategyFactory;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcRateLimiter;
import io.github.quiethappiness.wrench.traffic.control.types.enumvo.RateLimiterMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import static io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.getAttrValue;

@Slf4j
@Component("RateLimitSwitchNode")
@RequiredArgsConstructor
public class RateLimitSwitchNode extends AbstractRateLimiterSupport
{
	
	private final RateLimitBlackListNode RateLimitBlackListNode;
	
	private final RateLimitPPSNode RateLimitPPSNode;
	private final RateLimitSWRNode RateLimitSWRNode;
	
	/**
	 * 限流流程切换节点处理方法
	 * 该方法作为限流处理链的起始节点，负责：
	 * 1. 初始化限流上下文环境
	 * 2. 获取并验证限流注解配置
	 * 3. 解析限流关键字段值
	 * 4. 设置动态上下文信息供后续节点使用
	 * 5. 继续执行后续的限流处理流程
	 *
	 * @param requestParameter
	 * 	限流参数实体
	 * @param dynamicContext
	 * 	动态上下文环境，包含限流处理所需的所有信息
	 *
	 * @return 限流处理结果实体
	 *
	 * @throws Throwable
	 * 	处理过程中可能抛出的异常
	 */
	@Override
	protected RateLimiterReturnResultEntity doApply(RateLimiterParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Throwable
	{
		// 记录日志：开始执行限流处理流程的初始化阶段
		log.info("【RateLimitSwitchNode】:限流-开始");
		// 初始化限流决策标志为false，表示尚未进行限流决策
		// 这是为了确保在没有明确触发限流的情况下，默认不进行拦截
		dynamicContext.setDecideLimit(false);
		// 获取限流访问拦截器实例，用于获取限流相关的配置信息
		// 该实例包含了注解中定义的各种限流参数
		final TcRateLimiter tcRateLimiter = requestParameter.getTcRateLimiter();
		// 获取连接点信息，包含方法调用的相关上下文信息
		// 包括方法签名、参数列表等，用于解析限流字段值
		final ProceedingJoinPoint jp = requestParameter.getJp();
		// 获取限流注解中的key属性值，该值用于生成限流的唯一标识符
		// 这个key通常是一个SpEL表达式，用于从方法参数中提取特定字段
		String key = tcRateLimiter.key();
		// 判断key是否为空或空白字符，如果为空则抛出运行时异常
		// 这是必要的参数校验，确保限流逻辑能够正确执行
		if (StringUtils.isBlank(key))
		{
			throw new RuntimeException("annotation RateLimiter uId is null！");
		}
		// 根据key和方法参数获取具体的限流字段值
		// 通过解析SpEL表达式，从方法参数中提取实际的限流标识
		String keyAttr = getAttrValue(key, jp.getArgs());
		// 记录日志：获取到AOP限流字段的值，便于调试和监控
		log.info("【RateLimitSwitchNode】:限流-获取aop attr {}", keyAttr);
		// 将获取到的限流字段值设置到动态上下文中，供后续流程节点使用
		// 这个值将成为后续所有限流判断的基础标识
		dynamicContext.setKeyAttr(keyAttr);
		// 调用路由方法，根据限流参数和上下文信息继续执行后续的限流处理
		// 这里会将控制权交给下一个节点，形成完整的处理链路
		return router(requestParameter, dynamicContext);
	}
	
	@Override
	public StrategyHandler<RateLimiterParameterEntity, RateLimiterStrategyFactory.DynamicContext, RateLimiterReturnResultEntity> get(RateLimiterParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		final TcRateLimiter interceptor = requestParameter.getTcRateLimiter();
		RateLimiterMode mode = interceptor.mode();
		if (mode.equals(RateLimiterMode.PPS_BLACKLIST) || mode.equals(RateLimiterMode.SWR_BLACKLIST))
		{
			log.warn("【RateLimitSwitchNode】:限流-即将进入黑名单节点");
			return RateLimitBlackListNode;
		}
		else if (mode.equals(RateLimiterMode.PPS))
		{
			log.warn("【RateLimitSwitchNode】:限流-即将进入PPS节点");
			return RateLimitPPSNode;
		}
		else if (mode.equals(RateLimiterMode.SWR))
		{
			log.warn("【RateLimitSwitchNode】:限流-即将进入SWR节点");
			return RateLimitSWRNode;
		}
		log.error("【RateLimitSwitchNode】:限流-未知模式,使用默认处理器");
		return defaultStrategyHandler;
	}
}