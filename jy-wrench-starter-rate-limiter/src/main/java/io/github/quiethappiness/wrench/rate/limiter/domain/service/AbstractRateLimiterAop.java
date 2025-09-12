package io.github.quiethappiness.wrench.rate.limiter.domain.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * AbstractRateLimiterAop
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 抽象实现
 * @date 2025/9/12 15:54
 */
public abstract class AbstractRateLimiterAop implements IRateLimiterAOP
{
	// 个人限频记录1分钟
	protected final Cache<String, RateLimiter> loginRecord = CacheBuilder.newBuilder()
		.expireAfterWrite(1, TimeUnit.MINUTES)
		.build();
	
	// 个人限频黑名单24h - 分布式业务场景，可以记录到 Redis 中
	protected final Cache<String, Long> blacklist = CacheBuilder.newBuilder()
		.expireAfterWrite(24, TimeUnit.HOURS)
		.build();

	/**
	 * 调用用户配置的回调方法，当拦截后，返回回调结果。
	 */
	@Override
	public Object fallbackMethodResult(JoinPoint jp, String fallbackMethod) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		Signature sig = jp.getSignature();
		MethodSignature methodSignature = (MethodSignature) sig;
		Method method = jp.getTarget()
			.getClass()
			.getMethod(fallbackMethod, methodSignature.getParameterTypes());
		return method.invoke(jp.getThis(), jp.getArgs());
	}
}