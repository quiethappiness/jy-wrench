package io.github.quiethappiness.wrench.traffic.control.domain.model.entity;

import com.google.common.cache.Cache;
import com.google.common.util.concurrent.RateLimiter;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcRateLimiter;
import lombok.*;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * RateLimiterParameterEntity
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 请求参数
 * @date 2025/9/12 18:35
 */
@Getter
@RequiredArgsConstructor
@Builder
public class RateLimiterParameterEntity
{
	private final String rateLimiterSwitch;
	// 个人限频记录1分钟
	private final Cache<String, RateLimiter> loginRecord;
	
	// 个人限频黑名单24h - 分布式业务场景，可以记录到 Redis 中
	private final Cache<String, Long> blacklist;
	private final ProceedingJoinPoint jp;
	private final TcRateLimiter tcRateLimiter;
}