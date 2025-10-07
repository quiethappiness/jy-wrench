package io.github.quiethappiness.wrench.util.redisson.domain.impl;

import io.github.quiethappiness.wrench.util.redisson.domain.inter.*;
import org.redisson.api.RedissonClient;

/**
 * Redis 服务
 * @author quiethappiness @jingyue
 */
public interface IRedisService extends IRedisString, IRedisQueue, IRedisAtom, IRedisSet, IRedisList, IRedisMap, IRedisZSet, IRedisBit, IRedisThread, IRedisCommon,IRedisTopic
{
	static IRedisService defaultRedisService(RedissonClient redissonClient)
	{
		return new RedissonService(redissonClient);
	}

}