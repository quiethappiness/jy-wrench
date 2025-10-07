package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.business;

import io.github.quiethappiness.wrench.dynamic.config.center.config.DCCAutoProperties;
import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;
import io.github.quiethappiness.wrench.util.redisson.domain.impl.IRedisService;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.map.WriteMode;
import org.redisson.api.options.LocalCachedMapOptions;

import java.time.Duration;
import java.util.List;

import static io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.business.SlidingWindowRateLimiter.MAO_HAO;

public abstract class AbstractWhiteListService
{
	protected DCCAutoProperties DCCAutoProperties;
	
	protected IRedisService redisService;
	
	protected String spliceHashMapName(String name)
	{
		return DCCAutoProperties.getSystem() + MAO_HAO + "whitelist" + MAO_HAO + getClass().getSimpleName() + MAO_HAO + name;
	}
	
	protected static void setLocalCacheMapOptions(LocalCachedMapOptions<WhiteListType, List<String>> options)
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
	
	protected boolean doCheckFromRMap(WhiteListType type, String spliceHashMapName, String id)
	{
		LocalCachedMapOptions<WhiteListType, List<String>> options = LocalCachedMapOptions.<WhiteListType, List<String>>name(spliceHashMapName);
		setLocalCacheMapOptions(options);
		RLocalCachedMap<WhiteListType, List<String>> whiteList = redisService.getLocalCachedMap(options);
		List<String> strings = whiteList.get(type);
		return strings != null && strings.contains(id);
	}
	
	// @PostConstruct
	protected abstract void initWhitelist();
	
	public abstract boolean checkWhitelistId(String uri, WhiteListType type, String userId);
}