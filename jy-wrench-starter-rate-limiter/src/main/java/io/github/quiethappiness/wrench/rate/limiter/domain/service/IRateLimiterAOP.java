package io.github.quiethappiness.wrench.rate.limiter.domain.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Pointcut;

/**
 * IRateLimiterAOP
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 限流切面接口
 * @date 2025/9/12 15:52
 */
public interface IRateLimiterAOP
{
		/**
	 * 定义一个切点，用于拦截带有@RateLimiterAccessInterceptor注解的方法
	 * 该切点通过AspectJ的@Pointcut注解定义，匹配所有被指定注解标记的方法
	 */
	@Pointcut("@annotation(io.github.quiethappiness.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor)")
	default void aopPoint() {
	}
	
	/**
	 * 执行降级方法并返回结果
	 * 当原方法被限流或其他异常情况时，调用此方法执行降级逻辑
	 *
	 * @param jp 连接点对象，包含被拦截方法的详细信息
	 * @param fallbackMethod 降级方法的名称
	 * @return 降级方法执行后的返回结果
	 * @throws Exception 当降级方法执行过程中发生异常时抛出
	 */
	Object fallbackMethodResult(JoinPoint jp, String fallbackMethod) throws Exception;

	
}