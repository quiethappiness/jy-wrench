package io.github.quiethappiness.lua.manager.domain.service.redis.impl;

import io.github.quiethappiness.lua.manager.domain.service.redis.inter.*;
import org.redisson.api.RedissonClient;

/**
 * Redis 服务
 * @author Fuzhengwei bugstack.cn @小傅哥
 */
public interface IRedisService extends IRedisString, IRedisQueue, IRedisAtom, IRedisSet, IRedisList, IRedisMap, IRedisZSet, IRedisBit, IRedisThread, IRedisCommon,IRedisTopic
{
	static IRedisService defaultRedisService(RedissonClient redissonClient)
	{
		return new RedissonService(redissonClient);
	}

}