package io.github.quiethappiness.wrench.traffic.control.domain.service;

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
	@Pointcut("@annotation(io.github.quiethappiness.wrench.traffic.control.types.annotations.TcRateLimiter)")
	default void accessRateLimiterPointcut()
	{
	}
}