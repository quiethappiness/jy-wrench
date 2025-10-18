package io.github.quiethappiness.wrench.util.redisson.domain.base.inter;

public interface IRedisString
{
	/**
	 * 设置指定 key 的值
	 * @param key
	 * 	键
	 * @param value
	 * 	值
	 */
	<T> void setValue(String key, T value);
	
	/**
	 * 设置指定 key 的值
	 * @param key
	 * 	键
	 * @param value
	 * 	值
	 * @param expired
	 * 	过期时间
	 */
	<T> void setValue(String key, T value, long expired);
	
	/**
	 * 获取指定 key 的值
	 * @param key
	 * 	键
	 * @return 值
	 */
	<T> T getValue(String key);
}