package io.github.quiethappiness.lua.manager.domain.service.base.inter;
import org.redisson.api.options.LocalCachedMapOptions;
import org.redisson.api.*;

public interface IRedisMap
{
	/**
	 * 获取Map.对访问速度没有极端要求的情景，比如存储一些不常变的  共享配置
	 * @param key
	 * 	键
	 * @return 值
	 */
	<K, V> RMap<K, V> getMap(String key);//RMap：基础映射
	
	//RMapCache：支持过期淘汰
	
	/**
	 * 获取MapCache.为每个元素单独设置生存时间（TTL）和最大空闲时间  。Redis本身不支持Hash结构中元素的自动过期，Redisson通过一个后台调度器（EvictionScheduler）来清理过期数据。这使它非常适合存放  有时效性的数据  ，例如用户登录后的会话信息、短信验证码等，设置TTL后数据会自动失效并被清理
	 * @param name Redis中存储的MapCache名称
	 * @param <K> Map的键类型
	 * @param <V> Map的值类型
	 * @return RMapCache实例，支持元素过期和淘汰机制
	 */
	<K, V> RMapCache<K, V> getMapCache(String name);
	
	//RLocalCachedMap：本地缓存增强
	/**
	 * 为了应对"高频读取"场景的利器。它在客户端JVM内存中维护了一个本地缓存副本，使得后续的读取操作直接访问本地内存，读取速度相比直接访问Redis的RMap有显著提升，最多可提高45倍。它通过一定的策略（如SyncStrategy.INVALIDATE）来保证本地缓存与Redis主数据之间的最终一致性。最适合读多写少且对读取延迟敏感的数据，比如系统字典表、城市列表等基础数据
	 * @param options 本地缓存选项，用于配置缓存策略、大小、同步策略等
	 * @param <K> Map的键类型
	 * @param <V> Map的值类型
	 * @return RLocalCachedMap实例，支持本地缓存功能
	 */
	<K, V> RLocalCachedMap<K, V> getLocalCachedMap(LocalCachedMapOptions<K, V> options);
	

	/**
	 * 将指定的键值对添加到哈希表中
	 * @param key
	 * 	键
	 * @param field
	 * 	字段
	 * @param value
	 * 	值
	 */
	void addToMap(String key, String field, String value);
	
	/**
	 * 获取哈希表中指定字段的值
	 * @param key
	 * 	键
	 * @param field
	 * 	字段
	 * @return 值
	 */
	String getFromMap(String key, String field);
	
	/**
	 * 获取哈希表中指定字段的值
	 * @param key
	 * 	键
	 * @param field
	 * 	字段
	 * @return 值
	 */
	<K, V> V getFromMap(String key, K field);
}