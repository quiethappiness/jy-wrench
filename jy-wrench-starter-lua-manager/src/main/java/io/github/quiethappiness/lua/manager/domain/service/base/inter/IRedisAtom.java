package io.github.quiethappiness.lua.manager.domain.service.base.inter;

public interface IRedisAtom
{
	/**
	 * 设置值
	 * @param key
	 * 	key 键
	 * @param value
	 * 	值
	 */
	void setAtomicLong(String key, long value);
	
	/**
	 * 获取值
	 * @param key
	 * 	key 键
	 */
	Long getAtomicLong(String key);
	
	/**
	 * 自增 Key 的值；1、2、3、4
	 * @param key
	 * 	键
	 * @return 自增后的值
	 */
	long incr(String key);
	
	/**
	 * 指定值，自增 Key 的值；1、2、3、4
	 * @param key
	 * 	键
	 * @return 自增后的值
	 */
	long incrBy(String key, long delta);
	
	/**
	 * 自减 Key 的值；1、2、3、4
	 * @param key
	 * 	键
	 * @return 自增后的值
	 */
	long decr(String key);
	
	/**
	 * 指定值，自增 Key 的值；1、2、3、4
	 * @param key
	 * 	键
	 * @return 自增后的值
	 */
	long decrBy(String key, long delta);
}
