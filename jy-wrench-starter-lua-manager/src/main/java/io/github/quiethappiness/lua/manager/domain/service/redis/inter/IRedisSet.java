package io.github.quiethappiness.lua.manager.domain.service.redis.inter;

import org.redisson.api.RSet;
import org.redisson.api.RSetCache;
import org.redisson.api.RSetMultimap;
import org.redisson.api.RSetMultimapCache;

public interface IRedisSet
{
	/**
	 * 将指定的值添加到集合中
	 * @param key
	 * 	键
	 * @param value
	 * 	值
	 */
	void addToSet(String key, String value);
	
	/**
	 * 基础的分布式Set,元素自动去重，不支持元素级TTL.
	 * <p>
	 * 需要简单去重的集合，如标签、好友列表、独立访客统计
	 * @param name
	 * @return
	 * @param <V>
	 */
	<V> RSet<V> getSet(String name);
	
	/**
	 * 在RSet基础上，可为每个元素设置独立的过期时间（TTL）
	 * <p>
	 * 限时数据，如限时活动参与者、临时权限、会话黑名单
	 * @param name
	 * @return
	 * @param <V>
	 */
	<V> RSetCache<V> getSetCache(String name);
	
	//序列化：1.Redisson默认使用JsonJacksonCodec。如果你的对象存在循环引用，可能会导致序列化异常。此时可以考虑使用Fastjson等自定义编解码器
	// 2.过期事件的非实时性：基于TTL的过期清理不是实时的，这意味着contains检查可能在元素刚过期后一小段时间内仍返回true。对实时性要求极高的场景需谨慎评估
	// 3.分布式事务：这些集合都可以在Redisson的分布式事务中使用，保证多个操作的原子性
	/**
	 * 一个键（Key）对应一个`Set<V>`值的映射，键对应的值集合内元素去重
	 * <p>
	 * 需要分组且组内去重的数据，如班级学生列表（按班级分组，学号去重）
	 * <p>
	 *     Map<K, Set<V>>
	 * @param name
	 * @return
	 * @param <K>
	 * @param <V>
	 */
	<K, V> RSetMultimap<K, V> getSetMultimap(String name);
	
	/**
	 * 在RSetMultimap基础上，可为每个键对应的整个值集合设置过期时间
	 * <p>
	 * 需要过期的分组数据，如用户登录日志（按用户分组，定期清理）
	 * <p>
	 *     Map<K, Set<V>>（带键级过期）
	 * @param name
	 * @return
	 * @param <K>
	 * @param <V>
	 */
	<K, V> RSetMultimapCache<K, V> getSetMultimapCache(String name);
	
	/**
	 * 判断指定的值是否是集合的成员
	 * @param key
	 * 	键
	 * @param value
	 * 	值
	 * @return 如果是集合的成员返回 true，否则返回 false
	 */
	boolean isSetMember(String key, String value);
}
