package io.github.quiethappiness.wrench.util.redisson.domain;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.function.Supplier;

/**
 * @author jingyue @quiethappiness
 * @description 仓储抽象类
 */
@Slf4j
public abstract class AbstractRepository
{
	@Resource
	protected IRedisService redisService;
	
	/**
	 * 通用缓存处理方法
	 * 优先从缓存获取，缓存不存在则从数据库获取并写入缓存
	 * @param cacheKey
	 * 	缓存键
	 * @param dbFallback
	 * 	数据库查询函数
	 * @param <T>
	 * 	返回类型
	 * @return 查询结果
	 */
	protected <T> T getValueFromCacheOrDb(String cacheKey, Supplier<T> dbFallback)
	{
		// 判断是否开启缓存
		// 从缓存获取
		T cacheResult = redisService.getValue(cacheKey);
		// 缓存存在则直接返回
		if (null != cacheResult)
		{
			return cacheResult;
		}
		// 缓存不存在则从数据库获取
		T dbResult = dbFallback.get();
		// 数据库查询结果为空则直接返回
		if (null == dbResult)
		{
			return null;
		}
		// 写入缓存
		redisService.setValue(cacheKey, dbResult);
		return dbResult;
	}
	
	protected <T> T getValueFromCacheOrDb(Supplier<String> spliceCacheKey, Supplier<T> dbFallback)
	{
		// 判断是否开启缓存
		// 从缓存获取
		String cacheKey = spliceCacheKey.get();
		return getValueFromCacheOrDb(cacheKey, dbFallback);
	}
	
	/**
	 * 通用缓存处理方法（带过期时间）
	 * 优先从缓存获取，缓存不存在则从数据库获取并写入缓存
	 * @param cacheKey
	 * 	缓存键
	 * @param dbFallback
	 * 	数据库查询函数
	 * @param expired
	 * 	过期时间
	 * @param <T>
	 * 	返回类型
	 * @return 查询结果
	 */
	protected <T> T getValueFromCacheOrDb(String cacheKey, Supplier<T> dbFallback, long expired)
	{
		// 从缓存获取
		T cacheResult = redisService.getValue(cacheKey);
		// 缓存存在则直接返回
		if (null != cacheResult)
		{
			return cacheResult;
		}
		// 缓存不存在则从数据库获取
		T dbResult = dbFallback.get();
		// 数据库查询结果为空则直接返回
		if (null == dbResult)
		{
			return null;
		}
		// 写入缓存（带过期时间）
		redisService.setValue(cacheKey, dbResult, expired);
		return dbResult;
	}
}