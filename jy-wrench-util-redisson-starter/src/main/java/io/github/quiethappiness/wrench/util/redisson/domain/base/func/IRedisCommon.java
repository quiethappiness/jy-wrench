package io.github.quiethappiness.wrench.util.redisson.domain.base.func;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RFuture;
import org.redisson.api.RKeys;

import java.util.Collection;

public interface IRedisCommon
{
	public static final String EXPIRE = "_expire";
	
	<T>RBucket<T> getBucket(String key);
	RKeys getKey();
	
	/**
	 * 移除指定 key 的值
	 * @param key
	 * 	键
	 */
	void remove(String key);
	// 方式1：使用RKeys接口
	void deleteKey(String... keyArray);
	
	// 方式2：使用RBucket接口
	void deleteBucket(String key);
	
	// 方式3：批量删除
	void deleteKeys(Collection<String> keys);
	
	// 异步删除
	RFuture<Long> deleteAsync(String key);
	
	// 异步批量删除
	RFuture<Long> deleteByPatternAsync(String pattern);
	/**
	 * 判断指定 key 的值是否存在
	 * @param key
	 * 	键
	 * @return true/false
	 */
	boolean isExists(String key);
	
	/**
	 * 布隆过滤器
	 * @param key
	 * 	键
	 * @param <T>
	 * 	存放对象
	 * @return 返回结果
	 */
	<T> RBloomFilter<T> getBloomFilter(String key);

}