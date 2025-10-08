package io.github.quiethappiness.wrench.util.redisson.domain;

import io.github.quiethappiness.wrench.util.redisson.domain.inter.*;

/**
 * Redis 服务
 * @author quiethappiness @jingyue
 */
public interface IRedisService extends IRedisString, IRedisQueue, IRedisAtom, IRedisSet, IRedisList, IRedisMap, IRedisZSet, IRedisBit, IRedisThread, IRedisCommon,IRedisTopic
{

}