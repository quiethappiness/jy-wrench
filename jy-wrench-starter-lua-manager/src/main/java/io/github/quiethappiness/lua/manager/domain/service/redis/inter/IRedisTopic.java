package io.github.quiethappiness.lua.manager.domain.service.redis.inter;

import org.redisson.api.RTopic;

public interface IRedisTopic
{
	RTopic getTopic(String key);
}
