package io.github.quiethappiness.lua.manager.domain.service.base.impl;

import lombok.RequiredArgsConstructor;
import org.redisson.api.*;
import org.redisson.api.options.LocalCachedMapOptions;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Redis 服务 - Redisson
 * @author Fuzhengwei bugstack.cn @小傅哥
 */

@RequiredArgsConstructor(access = lombok.AccessLevel.PACKAGE)
public class RedissonService  implements IRedisService
{
	private final RedissonClient redissonClient;
	
	@Override
	public RKeys getKey()
	{
		return this.redissonClient.getKeys();
	}
	
	public <T> void setValue(String key, T value)
	{
		redissonClient.<T>getBucket(key)
			.set(value);
	}
	
	@Override
	public <T> void setValue(String key, T value, long expired)
	{
		RBucket<T> bucket = redissonClient.getBucket(key);
		bucket.set(value, Duration.ofMillis(expired));
	}
	
	public <T> T getValue(String key)
	{
		return redissonClient.<T>getBucket(key)
			.get();
	}
	
	@Override
	public <T> RQueue<T> getQueue(String key)
	{
		return redissonClient.getQueue(key);
	}
	
	@Override
	public <T> RBlockingQueue<T> getBlockingQueue(String key)
	{
		return redissonClient.getBlockingQueue(key);
	}
	
	@Override
	public <T> RDelayedQueue<T> getDelayedQueue(RBlockingQueue<T> rBlockingQueue)
	{
		return redissonClient.getDelayedQueue(rBlockingQueue);
	}
	@Override
	public void setAtomicLong(String key, long value)
	{
		redissonClient.getAtomicLong(key)
			.set(value);
	}
	
	@Override
	public Long getAtomicLong(String key)
	{
		return redissonClient.getAtomicLong(key)
			.get();
	}
	
	@Override
	public long incr(String key)
	{
		return redissonClient.getAtomicLong(key)
			.incrementAndGet();
	}
	
	@Override
	public long incrBy(String key, long delta)
	{
		return redissonClient.getAtomicLong(key)
			.addAndGet(delta);
	}
	
	@Override
	public long decr(String key)
	{
		return redissonClient.getAtomicLong(key)
			.decrementAndGet();
	}
	
	@Override
	public long decrBy(String key, long delta)
	{
		return redissonClient.getAtomicLong(key)
			.addAndGet(-delta);
	}
	
	@Override
	public RTopic getTopic(String key)
	{
		return redissonClient.getTopic(key);
	}
	
	@Override
	public void remove(String key)
	{
		redissonClient.getBucket(key)
			.delete();
	}
	
	@Override
	public boolean isExists(String key)
	{
		return redissonClient.getBucket(key)
			.isExists();
	}
	
	@Override
	public <V> RSortedSet<V> getSortedSet(String name)
	{
		return redissonClient.getSortedSet(name);
	}
	
	@Override
	public <V> RScoredSortedSet<V> getScoredSortedSet(String name)
	{
		return redissonClient.getScoredSortedSet(name);
	}
	
	@Override
	public RLexSortedSet getLexSortedSet(String name)
	{
		return redissonClient.getLexSortedSet(name);
	}
	
	@Override
	public <K, V> RListMultimap<K, V> getListMultimap(String name)
	{
		return redissonClient.getListMultimap(name);
	}
	
	@Override
	public <K, V> RListMultimapCache<K, V> getListMultimapCache(String name)
	{
		return redissonClient.getListMultimapCache(name);
	}
	
	@Override
	public <V> RList<V> getList(String name)
	{
		return redissonClient.getList(name);
	}
	
	@Override
	public <V> RSetCache<V> getSetCache(String name)
	{
		return redissonClient.getSetCache(name);
	}
	
	@Override
	public <V> RSet<V> getSet(String name)
	{
		return redissonClient.getSet(name);
	}
	
	public void addToSet(String key, String value)
	{
		RSet<String> set = redissonClient.getSet(key);
		set.add(value);
	}
	
	public boolean isSetMember(String key, String value)
	{
		RSet<String> set = redissonClient.getSet(key);
		return set.contains(value);
	}
	
	public void addToList(String key, String value)
	{
		RList<String> list = redissonClient.getList(key);
		list.add(value);
	}
	
	public String getFromList(String key, int index)
	{
		RList<String> list = redissonClient.getList(key);
		return list.get(index);
	}
	
	@Override
	public <K, V> RMap<K, V> getMap(String key)
	{
		return redissonClient.getMap(key);
	}
	
	@Override
	public <K, V> RMapCache<K, V> getMapCache(String name)
	{
		return redissonClient.getMapCache(name);
	}
	@Override
	public <K, V> RLocalCachedMap<K, V> getLocalCachedMap( LocalCachedMapOptions<K, V> options)
	{
		return redissonClient.getLocalCachedMap(options);
	}
	
	@Override
	public <K, V> RSetMultimapCache<K, V> getSetMultimapCache(String name)
	{
		return redissonClient.getSetMultimapCache(name);
	}
	
	@Override
	public <K, V> RSetMultimap<K, V> getSetMultimap(String name)
	{
		return redissonClient.getSetMultimap(name);
	}
	
	public void addToMap(String key, String field, String value)
	{
		RMap<String, String> map = redissonClient.getMap(key);
		map.put(field, value);
	}
	
	public String getFromMap(String key, String field)
	{
		RMap<String, String> map = redissonClient.getMap(key);
		return map.get(field);
	}
	
	@Override
	public <K, V> V getFromMap(String key, K field)
	{
		return redissonClient.<K, V>getMap(key)
			.get(field);
	}
	
	public void addToSortedSet(String key, String value)
	{
		RSortedSet<String> sortedSet = redissonClient.getSortedSet(key);
		sortedSet.add(value);
	}
	
	@Override
	public RLock getLock(String key)
	{
		return redissonClient.getLock(key);
	}
	
	@Override
	public RLock getFairLock(String key)
	{
		return redissonClient.getFairLock(key);
	}
	
	@Override
	public RReadWriteLock getReadWriteLock(String key)
	{
		return redissonClient.getReadWriteLock(key);
	}
	
	@Override
	public RSemaphore getSemaphore(String key)
	{
		return redissonClient.getSemaphore(key);
	}
	
	@Override
	public RPermitExpirableSemaphore getPermitExpirableSemaphore(String key)
	{
		return redissonClient.getPermitExpirableSemaphore(key);
	}
	
	@Override
	public RCountDownLatch getCountDownLatch(String key)
	{
		return redissonClient.getCountDownLatch(key);
	}
	
	@Override
	public <T> RBloomFilter<T> getBloomFilter(String key)
	{
		return redissonClient.getBloomFilter(key);
	}
	
	@Override
	public Boolean setNx(String key)
	{
		return redissonClient.getBucket(key)
			.trySet("lock");
	}
	
	@Override
	public Boolean setNx(String key, long expired, TimeUnit timeUnit)
	{
		return redissonClient.getBucket(key)
			.trySet("lock", expired, timeUnit);
	}
	@Override
	public RBitSet getBitSet(String key)
	{
		return redissonClient.getBitSet(key);
	}

}