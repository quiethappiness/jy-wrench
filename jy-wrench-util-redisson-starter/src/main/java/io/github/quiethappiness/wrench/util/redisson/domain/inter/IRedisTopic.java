package io.github.quiethappiness.wrench.util.redisson.domain.inter;

import org.redisson.api.RTopic;

public interface IRedisTopic
{
	RTopic getTopic(String key);
}