package io.github.quiethappiness.wrench.rate.limiter.types.annotations;

import java.lang.annotation.*;

/**
 * RateLimiterAccessInterceptor
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 限流拦截器
 * @date 2025/9/12 15:38
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RateLimiterAccessInterceptor
{
	/**
	 * 用哪个字段作为拦截标识，未配置则默认走全部
	 */
	String key() ;
	
	/**
	 * 是否开启限制PPS
	 */
	boolean enabledPPS() default true;
	
	/**
	 * PPS限制频次规则（每秒请求次数）
	 */
	double permitsPerSecond() default 100;
	
	/**
	 * 是否开启限制黑名单
	 */
	boolean enableBlacklist() default true;
	
	/**
	 * 黑名单拦截规则（多少次限制后加入黑名单）0 不限制
	 */
	double blacklistCount() default 10;
	
	/**
	 * 拦截后的执行方法
	 */
	String fallbackMethod();
}