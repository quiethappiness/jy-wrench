package io.github.quiethappiness.lua.manager.domain.service.base.inter;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RKeys;

import java.util.concurrent.TimeUnit;

public interface IRedisCommon
{
	public static final String EXPIRE = "_expire";
	
	RKeys getKey();
	
	/**
	 * 移除指定 key 的值
	 * @param key
	 * 	键
	 */
	void remove(String key);
	
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
	
	Boolean setNx(String key);
	
	Boolean setNx(String key, long expired, TimeUnit timeUnit);
}
