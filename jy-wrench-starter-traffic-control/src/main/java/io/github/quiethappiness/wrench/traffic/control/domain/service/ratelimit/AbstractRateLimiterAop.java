package io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import io.github.quiethappiness.wrench.traffic.control.domain.service.IRateLimiterAOP;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

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
	 * 执行降级方法并返回结果
	 * @param jp
	 * 	连接点对象，包含目标方法的签名和执行信息
	 * @param fallbackMethod
	 * 	降级方法名称
	 * @return 降级方法执行后的返回结果
	 * @throws Exception
	 * 	当方法调用过程中发生异常时抛出
	 */
	@Override
	public Object fallbackMethodResult(JoinPoint jp, String fallbackMethod) throws Exception
	{
		// 获取目标方法的签名信息
		// 获取被拦截方法的签名信息
		// Signature 是 AspectJ 中的一个接口，表示程序中各种元素（如方法、构造函数、字段等）的签名
		// 它包含了被拦截元素的基本信息，如名称、声明类型等
		// 可以确保找到的方法签名与原始方法一致，从而正确地调用降级处理方法
		Signature sig = jp.getSignature();
		// 将通用的 Signature 对象转换为 MethodSignature 类型
		// MethodSignature 是 AspectJ 提供的一个接口，专门用于表示方法级别的签名信息
		// 通过转换为 MethodSignature，可以获得更详细的方法信息，如：
		// - 方法参数类型列表
		// - 返回值类型
		// - 方法名称等
		MethodSignature methodSignature = (MethodSignature) sig;
		// 通过反射获取降级方法并执行
		Method method = jp.getTarget()
			.getClass()
			.getMethod(fallbackMethod, methodSignature.getParameterTypes());
		// invoke() 方法用于执行指定对象上的方法
		return method.invoke(jp.getThis(), jp.getArgs());
	}
}