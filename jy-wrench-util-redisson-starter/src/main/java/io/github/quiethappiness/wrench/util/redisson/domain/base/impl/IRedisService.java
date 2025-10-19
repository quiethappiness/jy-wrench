package io.github.quiethappiness.wrench.util.redisson.domain.base.impl;

import io.github.quiethappiness.wrench.util.redisson.domain.base.inter.*;
import org.redisson.api.RFuture;

import java.util.Collection;

/**
 * Redis 服务
 * @author quiethappiness @jingyue
 */
public interface IRedisService extends IRedisString, IRedisQueue, IRedisAtom, IRedisSet, IRedisList, IRedisMap, IRedisZSet, IRedisBit, IRedisThread, IRedisCommon, IRedisTopic
{
	

	

}