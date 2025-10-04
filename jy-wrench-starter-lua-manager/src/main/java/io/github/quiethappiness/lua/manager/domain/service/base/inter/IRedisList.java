package io.github.quiethappiness.lua.manager.domain.service.base.inter;

import org.redisson.api.RList;
import org.redisson.api.RListMultimap;
import org.redisson.api.RListMultimapCache;

public interface IRedisList
{
	/**
	 * 将指定的值添加到列表中
	 * @param key
	 * 	键
	 * @param value
	 * 	值
	 */
	void addToList(String key, String value);
	
	/**
	 * 基础的分布式List，元素有序且允许重复
	 * <p>
	 * 需要保持顺序的列表，如消息队列、时间线、日志记录
	 * @param name
	 * @return
	 * @param <V>
	 */
	<V> RList<V> getList(String name);
	// 1.性能考虑：对于 RList和 RListMultimap中的列表，通过索引（如 get(index)）访问元素的时间复杂度是 O(N)，在大列表上操作可能较慢。如需按分数快速访问，应考虑RScoredSortedSet
	// 2.过期清理机制：基于TTL的过期清理（如在 RListMultimapCache中）不是实时的。Redisson通过后台任务定期清理过期元素，这意味着过期后可能不会立即被移除，会有一定的延迟
	// 3.序列化：确保存储在其中的对象已正确序列化。Redisson默认使用JsonJacksonCodec，如果遇到循环引用问题，可能需要配置自定义的编解码器
	
	/**
	 * 一个键（Key）对应一个List<V>值的映射，键对应的值集合内元素有序且允许重复
	 * <p>
	 *     需要分组且组内有序的数据，如按用户分类的日志列表（同一用户多条日志）
	 * <p>
	 *     Map<K, List<V>>
	 * @param name
	 * @return
	 * @param <K>
	 * @param <V>
	 */
	<K, V> RListMultimap<K, V> getListMultimap(String name);
	
	/**
	 * 在RListMultimap基础上，可为每个键对应的整个值集合设置过期时间
	 * <p>
	 *     需要过期的分组有序数据，如用户临时消息草稿（按用户分组，定期清理）
	 *     <p>
	 *     Map<K, List<V>>（带键级过期）
	 * @param name
	 * @param name
	 * @return
	 * @param <K>
	 * @param <V>
	 */
	<K, V> RListMultimapCache<K, V> getListMultimapCache(String name);
	/**
	 * 获取列表中指定索引的值
	 * @param key
	 * 	键
	 * @param index
	 * 	索引
	 * @return 值
	 */
	String getFromList(String key, int index);
}
