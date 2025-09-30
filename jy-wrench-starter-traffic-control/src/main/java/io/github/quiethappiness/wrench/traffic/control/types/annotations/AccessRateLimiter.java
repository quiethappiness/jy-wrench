package io.github.quiethappiness.wrench.traffic.control.types.annotations;

import io.github.quiethappiness.wrench.traffic.control.types.enumvo.TrafficMode;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * AccessRateLimiter
 *
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 限流拦截器
 * @date 2025/9/12 15:38
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface AccessRateLimiter
{
	/**
	 * 用哪个字段作为拦截标识，未配置则默认走全部
	 */
	String key();
	
	/**
	 * 限流模式
	 */
	TrafficMode mode() default TrafficMode.PPS_BLACKLIST;
	
	/**
	 * 拦截后的执行方法
	 */
	String fallbackMethod();
	
	// 以下为 PPS 模式下的参数--------------------------------------------------
	/**
	 * PPS限制频次规则（每秒请求次数）
	 */
	double permitsPerSecond() default 50;
	
	/**
	 * 预热期，在预热期内不进行限制
	 */
	long warmupPeriod() default 3;
	
	/**
	 * 预热期时间单位
	 */
	TimeUnit unit() default TimeUnit.SECONDS;
	
	// 以下为 SWR 模式下的参数--------------------------------------------------
	/**
	 * 滑动窗口大小，单位毫秒
	 */
	long windowSizeMs() default 1000;
	
	/**
	 * 最大请求数
	 * 超过最大请求数则直接拦截
	 */
	long maxRequests() default 50;
	
	// 以下为 含有BLACKLIST 模式下的参数--------------------------------------------------
	/**
	 * 黑名单拦截规则（多少次限制后加入黑名单）0 不限制
	 */
	double blacklistCount() default 10;
	

}