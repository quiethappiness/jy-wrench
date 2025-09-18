package io.github.quiethappiness.wrench.sample;

import java.util.concurrent.TimeUnit;

/**
 * IRedisWithLua
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 自己写的接口和实现
 * @date 2025/9/9 15:33
 */
public interface IRedisWithLua
{
	long incrWithTtl(String key, long delta, long ttl, TimeUnit unit);
	
	void setWithTtl(String key, long value, long ttl, TimeUnit unit);
}