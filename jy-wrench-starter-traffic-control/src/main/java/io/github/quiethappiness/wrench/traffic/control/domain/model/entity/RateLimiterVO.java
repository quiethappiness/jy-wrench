package io.github.quiethappiness.wrench.traffic.control.domain.model.entity;

import com.google.common.cache.Cache;
import com.google.common.util.concurrent.RateLimiter;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcRateLimiter;
import lombok.Builder;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * RateLimiterVO
 * @description ratelimit实体
 * @author quietHappiness @jingyue
 * @date 2025/11/2 10:21
 * @version 1.0
 */
public record RateLimiterVO()
{
	/**
	 * ReturnResultEntity
	 * @author quietHappiness @jingyue
	 * @version 1.0
	 * @description 返回值
	 * @date 2025/9/12 19:14
	 */
	@Builder
	public record ReturnResultEntity(boolean decideLimit)
	{
	}
	
	/**
	 * ParameterEntity
	 * @param loginRecord
	 * 	个人限频记录1分钟
	 * @param blacklist
	 * 	个人限频黑名单24h - 分布式业务场景，可以记录到 Redis 中
	 * @author quietHappiness @jingyue
	 * @version 1.0
	 * @description 请求参数
	 * @date 2025/9/12 18:35
	 */
	
	@Builder
	public record ParameterEntity(
		String rateLimiterSwitch,
		Cache<String, RateLimiter> loginRecord,
		Cache<String, Long> blacklist,
		ProceedingJoinPoint jp,
		TcRateLimiter tcRateLimiter)
	{
	}
}