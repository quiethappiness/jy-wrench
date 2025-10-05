package io.github.quiethappiness.wrench.lua.manager.domain.service.base.impl;

import io.github.quiethappiness.wrench.lua.manager.domain.service.base.inter.*;
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