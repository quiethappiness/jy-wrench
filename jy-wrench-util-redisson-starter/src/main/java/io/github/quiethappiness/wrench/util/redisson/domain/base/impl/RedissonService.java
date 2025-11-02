package io.github.quiethappiness.wrench.util.redisson.domain.base.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.redisson.api.map.WriteMode;
import org.redisson.api.options.LocalCachedMapOptions;
import org.redisson.api.stream.StreamCreateGroupArgs;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis 服务 - Redisson
 * @author quiethappiness @jingyue
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class RedissonService implements IRedisService
{
	private final RedissonClient redissonClient;
	
	{
		log.info("jy-wrench，注册器（RedissonService）初始化完成");
	}
	
	@Override
	public RRateLimiter getRateLimiter(String name)
	{
		return redissonClient.getRateLimiter(name);
	}
	
	@Override
	public RSearch getSearch()
	{
		return redissonClient.getSearch();
	}
	
	@Override
	public <T> void indexProduct(String id, T product)
	{
		RBucket<T> productBucket = redissonClient.getBucket(id);
		productBucket.set(product);
	}
	
	@Override
	public <V, L> RTimeSeries<V, L> getTimeSeries(String name)
	{
		return redissonClient.getTimeSeries(name);
	}
	
	@Override
	public <V, L> Collection<TimeSeriesEntry<V, L>> entryRange(String name, long startTimestamp, long endTimestamp)
	{
		return redissonClient.<V, L>getTimeSeries(name)
			.entryRange(startTimestamp, endTimestamp);
	}
	
	@Override
	public <V, L> Collection<TimeSeriesEntry<V, L>> entryRange(String name, long startTimestamp, long endTimestamp, int limit)
	{
		return redissonClient.<V, L>getTimeSeries(name)
			.entryRange(startTimestamp, endTimestamp, limit);
	}
	
	@Override
	public <K, V> RStream<K, V> getStream(String streamName)
	{
		return redissonClient.getStream(streamName);
	}
	
	@Override
	public <K, V> Map<StreamMessageId, Map<K, V>> range(String streamName, StreamMessageId startId, StreamMessageId endId)
	{
		return redissonClient.<K, V>getStream(streamName)
			.range(startId, endId);
	}
	
	@Override
	public void createGroup(String streamName, StreamCreateGroupArgs args)
	{
		redissonClient.getStream(streamName)
			.createGroup(args);
	}
	
	@Override
	public void createConsumer(String streamName, String groupName, String consumerName)
	{
		redissonClient.getStream(streamName)
			.createConsumer(groupName, consumerName);
	}
	
	@Override
	public void updateGroupMessageId(String streamName, String groupName, StreamMessageId id)
	{
		redissonClient.getStream(streamName)
			.updateGroupMessageId(groupName, id);
	}
	
	@Override
	public long ack(String streamName, String groupName, StreamMessageId... ids)
	{
		return 0;
	}
	
	@Override
	public <T> RBucket<T> getBucket(String key)
	{
		return redissonClient.getBucket(key);
	}
	
	@Override
	public RKeys getKey()
	{
		return this.redissonClient.getKeys();
	}
	
	@Override
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
	
	@Override
	public <T> void setValue(String key, T value, long expired, TimeUnit timeUnit)
	{
		RBucket<T> bucket = redissonClient.getBucket(key);
		bucket.set(value, Duration.ofMillis(TimeUnit.MILLISECONDS.convert(expired, timeUnit)));
	}
	
	@Override
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
	
	// 方式1：使用RKeys接口
	@Override
	public void deleteKey(String... keyArray)
	{
		RKeys keys = redissonClient.getKeys();
		keys.delete(keyArray);
	}
	
	// 方式2：使用RBucket接口
	@Override
	public void deleteBucket(String key)
	{
		RBucket<Object> bucket = redissonClient.getBucket(key);
		bucket.delete();
	}
	
	// 方式3：批量删除
	@Override
	public void deleteKeys(Collection<String> keys)
	{
		RKeys rKeys = redissonClient.getKeys();
		rKeys.delete(keys.toArray(new String[0]));
	}
	
	// 异步删除
	@Override
	public RFuture<Long> deleteAsync(String key)
	{
		RKeys keys = redissonClient.getKeys();
		return keys.deleteAsync(key);
	}
	
	// 异步批量删除
	@Override
	public RFuture<Long> deleteByPatternAsync(String pattern)
	{
		RKeys keys = redissonClient.getKeys();
		return keys.deleteByPatternAsync(pattern);
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
	public <K, V> RLocalCachedMap<K, V> getLocalCachedMap(LocalCachedMapOptions<K, V> options)
	{
		return redissonClient.getLocalCachedMap(options);
	}
	
	@Override
	public void set_LocalCacheMapOptions_Of_LargeDataVolumeType(LocalCachedMapOptions<?, ?> options)
	{
		options
			.evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LRU) // 库存数据量固定，不主动淘汰
			.storeMode(LocalCachedMapOptions.StoreMode.LOCALCACHE_REDIS)
			.syncStrategy(LocalCachedMapOptions.SyncStrategy.INVALIDATE)  // 节省带宽
			.cacheSize(1000) // 可根据内存容量调整
			.timeToLive(Duration.ofMinutes(2)) // TTL较短，及时更新
			.maxIdle(Duration.ofMinutes(5))
			.reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR)// 重连后清空，保证会话一致性
			.writeMode(WriteMode.WRITE_BEHIND)
			.storeCacheMiss(true);
	}
	
	@Override
	public void set_LocalCacheMapOptions_Of_StrongConsistencyType(LocalCachedMapOptions<?, ?> options)
	{
		options
			.evictionPolicy(LocalCachedMapOptions.EvictionPolicy.NONE) // 库存数据量固定，不主动淘汰
			.storeMode(LocalCachedMapOptions.StoreMode.LOCALCACHE_REDIS)
			.syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE) // 或 INVALIDATE，确保强一致性
			.cacheSize(1000) // 可根据内存容量调整
			.timeToLive(Duration.ofMinutes(5)) // TTL较短，及时更新
			.maxIdle(Duration.ofMinutes(5))
			.reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR)// 重连后清空，保证会话一致性
			.writeMode(WriteMode.WRITE_BEHIND)
			.storeCacheMiss(false);
	}
	
	@Override
	public void set_LocalCacheMapOptions_Of_SessionManagement(LocalCachedMapOptions<?, ?> options)
	{
		options
			.evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LRU) // // 淘汰最久未使用的会话
			.storeMode(LocalCachedMapOptions.StoreMode.LOCALCACHE_REDIS)
			.syncStrategy(LocalCachedMapOptions.SyncStrategy.INVALIDATE) // 失效策略，平衡性能与一致性
			.cacheSize(5000) // 可根据内存容量调整
			.reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR)// 重连后清空，保证会话一致性
			.timeToLive(Duration.ofHours(2)) // 会话最大存活2小时
			.maxIdle(Duration.ofMinutes(30)) // 30分钟不活动则失效
			.writeMode(WriteMode.WRITE_BEHIND)
			.writeRetryAttempts(3) // 网络波动时重试3次
			.storeCacheMiss(true);// 缓存空结果，防穿透
	}
	
	@Override
	public void set_LocalCacheMapOptions_Of_HighFrequencyReading(LocalCachedMapOptions<?, ?> options)
	{
		options
			.evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LFU) // 优先保留热点商品
			.storeMode(LocalCachedMapOptions.StoreMode.LOCALCACHE_REDIS)
			.syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE) // 更新策略保证读性能
			.cacheSize(10000) // 可根据内存容量调整
			.timeToLive(Duration.ofMinutes(30)) // 数据有效时间较长
			.reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.LOAD)// 平衡一致性与性能
			.writeMode(WriteMode.WRITE_BEHIND)// 异步写入数据库，提升性能
			.writeRetryAttempts(5) // 网络波动时重试5次
			.storeCacheMiss(false);
	}
	
	@Override
	public void set_LocalCacheMapOptions(LocalCachedMapOptions<?, ?> options)
	{
		// 定义本地缓存的淘汰策略，如LRU（最近最少使用）、LFU（最不经常使用）[3,5](@ref)
		options.evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LRU)
			// 本地缓存的最大容量，如果缓存数量超过此值，会根据淘汰策略移除元素[1,7](@ref)
			.cacheSize(1000)
			// 定义本地缓存与Redis主数据之间的同步策略[3,5](@ref)
			// INVALIDATE: 当数据在Redis中更新时，使所有实例中的该缓存条目失效（默认）
			// UPDATE: 当数据在Redis中更新时，将新值推送到所有实例的本地缓存
			// NONE: 不进行同步
			.syncStrategy(LocalCachedMapOptions.SyncStrategy.INVALIDATE)
			// 定义与Redis连接断开并重新建立后的处理策略[3,5](@ref)
			// CLEAR: 清空本地缓存，确保从Redis重新加载最新数据
			// LOAD: 尝试根据服务端保存的失效日志更新本地缓存
			// NONE: 不做处理
			.reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.NONE)
			.writeMode(WriteMode.WRITE_BEHIND)
			// 本地缓存条目的生存时间（TTL）[1,7](@ref)
			.timeToLive(Duration.ofHours(12))
			// 本地缓存条目的最大空闲时间[1,7](@ref)
			.maxIdle(Duration.ofMinutes(5))
			.writeRetryAttempts(10)
		;
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
			.setIfAbsent("lock");
	}
	
	@Override
	public Boolean setNx(String key, long expired, TimeUnit timeUnit)
	{
		Duration duration = Duration.ofMillis(timeUnit.toMillis(expired));
		return setNx(key, duration);
	}
	
	@Override
	public Boolean setNx(String key, Duration duration)
	{
		return redissonClient.getBucket(key)
			.setIfAbsent("lock", duration);
	}
	
	@Override
	public RBitSet getBitSet(String key)
	{
		return redissonClient.getBitSet(key);
	}
}