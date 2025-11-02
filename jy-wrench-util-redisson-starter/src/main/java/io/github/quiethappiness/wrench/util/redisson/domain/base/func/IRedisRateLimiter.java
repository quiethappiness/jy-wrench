package io.github.quiethappiness.wrench.util.redisson.domain.base.func;

import org.redisson.api.RRateLimiter;

/**
 * IRedisRateLimiter
 * @description 限流组件
 * @author quietHappiness @jingyue
 * @date 2025/11/2 16:55
 * @version 1.0
 */
public interface IRedisRateLimiter
{
	RRateLimiter getRateLimiter(String name);
}