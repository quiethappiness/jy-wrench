package io.github.quiethappiness.wrench.util.redisson.domain.base.inter;

import org.redisson.api.RTopic;

public interface IRedisTopic
{
	RTopic getTopic(String key);
}