package io.github.quiethappiness.wrench.util.redisson.domain.base.impl;

import io.github.quiethappiness.wrench.util.redisson.domain.base.func.*;
import io.github.quiethappiness.wrench.util.redisson.domain.base.structure.*;

/**
 * Redis 服务
 * @author quiethappiness @jingyue
 */
public interface IRedisService extends IRedisString, IRedisQueue, IRedisAtom, IRedisSet, IRedisList, IRedisMap, IRedisZSet, IRedisBit, IRedisThread, IRedisCommon, IRedisTopic, IRedisTimeSeries,IRedisStream,IRedisSearch,IRedisRateLimiter
{
	
}