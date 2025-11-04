package io.github.quiethappiness.wrench.traffic.control.types.annotations;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * TcRateLimiter
 *
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 限流拦截器
 * @date 2025/9/12 15:38
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface TcRateLimiter
{
	/**
	 * 用哪个字段作为拦截标识，未配置则报错
	 */
	String key();
	
	/**
	 * 限流模式
	 */
	RateLimiterMode mode() default RateLimiterMode.PPS_BLACKLIST;
	
	/**
	 * 拦截后的执行方法
	 */
	String fallbackMethod();
	
	// 以下为 PPS 模式下的参数--------------------------------------------------
	/**
	 * PPS限制频次规则（每秒请求次数）（对于单个实例）
	 */
	double permitsPerSecond() default 3;
	
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
	
	@AllArgsConstructor
	@Getter
	enum RateLimiterMode
	{
		/**
		 * 白名单模式，白名单内的用户不受限，其他用户受限
		 */
		// WHITELIST("whitelist", "Whitelist mode"),
		/**
		 * 频率模式，用户请求的频率超过限制则被限制
		 */
		PPS("pps", "Requests per second"),
		SWR("swr", "Slide window rate limiter"),
		/**
		 * 混合模式：
		 */
		// PPS+
		PPS_BLACKLIST("pps_blacklist", "Requests per second with blacklist"),
		SWR_BLACKLIST("swr_blacklist", "Slide window rate limiter with blacklist"),
		// WHITELIST_PPS("whitelist_pps", "whitelist mode with Requests per second"),
		// WHITELIST_PPS_BLACKLIST("whitelist_pps_blacklist", "whitelist mode with Requests per second with blacklist"),
		// WHITELIST_SWR("whitelist_swr", "whitelist mode with Slide window rate limiter"),
		// WHITELIST_SWR_BLACKLIST("whitelist_swr_blacklist", "whitelist mode with Slide window rate limiter with blacklist"),
		;
		private final String name;
		private final String desc;
	}
}