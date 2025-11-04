package io.github.quiethappiness.wrench.traffic.control.types.annotations;

import java.lang.annotation.*;

/**
 * 幂等性注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TcIdempotent
{
	
	/**
	 * 令牌在请求头中的字段名
	 */
	String tokenHeader() default "Idempotency-Token";
	
	/**
	 * 幂等性级别
	 */
	Level level() default Level.NORMAL;
	
	/**
	 * 重复请求时的错误消息
	 */
	String message() default "请勿重复提交";
	
	// 仅用于normal,strict级别
	/**
	 * 请求唯一标识，用于生成幂等性令牌以及其他频次限制
	 */
	String rateMark() default "userId";
	/**
	 * 频次限制(对于一整个服务）
	 */
	int rateLimit() default 10;
	
	/**
	 * 频率限制时间,单位s
	 */
	long rateLimitDuration() default 5;
	
	/*
	  幂等性
	  */
	enum Level
	{
		/**
		 快速接口：不限制时间间隔，只要令牌有效即可
		 */
		FAST,
		/**
		 * 常规接口：会进行基本的频率检查
		 */
		NORMAL,
		/**
		 * 严格接口：会进行严格的相似性检查,相似性检查是根据equals方法进行的，因此需要保证对象的一致性
		 */
		STRICT
	}
}