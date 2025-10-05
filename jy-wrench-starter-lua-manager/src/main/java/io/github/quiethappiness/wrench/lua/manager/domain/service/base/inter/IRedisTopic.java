package io.github.quiethappiness.wrench.lua.manager.domain.service.base.inter;

import org.redisson.api.RTopic;

public interface IRedisTopic
{
	RTopic getTopic(String key);
}
