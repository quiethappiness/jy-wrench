package io.github.quiethappiness.wrench.traffic.control.types.annotations;

import java.lang.annotation.*;

/**
 * 幂等性注解.
 * <p>针对于一些新建类请求，可以考虑使用幂等性注解。防止重复创建。</p>
 * <p>当然，数据库方面同时也需要进行<code>服务id</code>的唯一性约束</p>
 * 自定义注解：TcIdempotent，用于标记需要实现幂等性控制的方法
 * 该注解可以应用于方法级别，运行时保留，并且会被包含在JavaDoc中
 */
@Target(ElementType.METHOD)  // 指定该注解只能用于方法上
@Retention(RetentionPolicy.RUNTIME)  // 指定该注解在运行时仍然保留
@Documented  // 指定该注解会被包含在JavaDoc中
public @interface TcIdempotent
{
	
	/**
	 * 令牌在请求头中的字段名
	 */
	String tokenHeader() default "Idempotency-Token";  // 默认值为"Idempotency-Token"
	
	/**
	 * 幂等性级别
	 */
	Level level() default Level.NORMAL;  // 默认级别为NORMAL
	
	/**
	 * 重复请求时的错误消息
	 */
	String message() default "请勿重复提交";  // 默认错误消息为"请勿重复提交"
	
	// 仅用于normal,strict级别
	/**
	 * 请求唯一标识，用于生成幂等性令牌以及其他频次限制，比如用户ID
	 * <p>spel表达式</p>
	 * <pre>
	 *     #{user.id}
	 * </pre>
	 */
	String rateMark() ;  // 必填项，用于指定请求的唯一标识
	/**
	 * 频次限制(对于一整个服务）
	 */
	int rateLimit() default 10;  // 默认频次限制为10次
	
	/**
	 * 频率限制时间,单位s
	 */
	long rateLimitDuration() default 5;  // 默认限制时间为5秒
	
	/*
	  幂等性级别枚举
	  */
	enum Level
	{
		/**
		 快速接口：不限制时间间隔，只要令牌有效即可
		 */
		FAST,  // 快速级别，不限制时间间隔
		/**
		 * 常规接口：会进行基本的频率检查
		 */
		NORMAL,  // 常规级别，进行基本的频率检查
		/**
		 * 严格接口：会进行基本的频率检查+会进行严格的相似性检查,相似性检查是根据equals方法进行的，因此需要保证对象的一致性
		 */
		STRICT  // 严格级别，进行频率检查和相似性检查
	}
}