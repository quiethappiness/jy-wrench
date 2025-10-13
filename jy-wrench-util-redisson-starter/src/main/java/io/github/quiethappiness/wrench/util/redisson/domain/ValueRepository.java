package io.github.quiethappiness.wrench.util.redisson.domain;

import io.github.quiethappiness.wrench.util.redisson.domain.inter.IRedisString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author jingyue @quiethappiness
 * @description 仓储抽象类
 */
@Slf4j
@Repository
public final class ValueRepository
{
	@Resource
	private IRedisString redisString;
	
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
	public <T, R> R getSingle(String cacheKey, Supplier<T> dbFallback, Function<T, R> mapper, long expired, TimeUnit timeUnit)
	{
		// 判断是否开启缓存
		// 从缓存获取
		R cacheResult = redisString.getValue(cacheKey);
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
		cacheResult = mapper.apply(dbResult);
		// 写入缓存
		redisString.setValue(cacheKey, cacheResult, TimeUnit.MILLISECONDS.convert(expired, timeUnit));
		return cacheResult;
	}
	
	public <T, R> R getSingle(String cacheKey, Supplier<T> dbFallback, Function<T, R> mapper)
	{
		return getSingle(cacheKey, dbFallback, mapper, 12, TimeUnit.HOURS);
	}
	
	public <T, R> List<R> getList(String cacheKey, Supplier<List<T>> dbFallback, Function<T, R> mapper, long expired, TimeUnit timeUnit)
	{
		// 判断是否开启缓存
		// 从缓存获取
		// List<JSONObject> result = redisService.getValue(cacheKey);
		// if (!CollectionUtils.isEmpty(result))
		// {
		// 	// List<R> cacheResult = JSON.parseObject(string, List.class);
		// 	// 缓存存在则直接返回
		// 	return result.stream()
		// 		.map((jsonObject) -> JSON.parseObject(jsonObject.toString(), rClass))
		// 		.toList();
		// }
		Object value = redisString.getValue(cacheKey);
		log.info("从缓存获取结果：{}", value);
		List<R> cacheResult = null;
		if (value instanceof List)
		{
			cacheResult = (List<R>) value;
		}
		// log.info("从缓存获取结果：{}", cacheResult);
		if (!CollectionUtils.isEmpty(cacheResult))
		{
			return cacheResult;
		}
		// 缓存不存在则从数据库获取
		List<T> dbResult = dbFallback.get();
		// 数据库查询结果为空则直接返回
		if (CollectionUtils.isEmpty(dbResult))
		{
			return Collections.emptyList();
		}
		List<R> list = dbResult.stream()
			.map(mapper)
			.toList();
		// 写入缓存
		redisString.setValue(cacheKey, new ArrayList<>(list), TimeUnit.MILLISECONDS.convert(expired, timeUnit));
		return list;
	}
	
	public <T, R> List<R> getList(String cacheKey, Supplier<List<T>> dbFallback, Function<T, R> mapper)
	{
		return getList(cacheKey, dbFallback, mapper, 12, TimeUnit.HOURS);
	}

	public <K, V> Map<K, V> getMap(String cacheKey, Supplier<Map<K, V>> dbFallback, long expired, TimeUnit timeUnit)
	{
		Object value = redisString.getValue(cacheKey);
		log.info("从缓存获取结果：{}", value);
		Map<K, V> cacheResult = null;
		if (value instanceof Map<?, ?>)
		{
			cacheResult = (Map<K, V>) value;
		}
		// log.info("从缓存获取结果：{}", cacheResult);
		if (!CollectionUtils.isEmpty(cacheResult))
		{
			return cacheResult;
		}
		// 缓存不存在则从数据库获取
		Map<K, V> dbResult = dbFallback.get();
		// 数据库查询结果为空则直接返回
		if (CollectionUtils.isEmpty(dbResult))
		{
			return Collections.emptyMap();
		}
		// 写入缓存
		redisString.setValue(cacheKey, new HashMap<>(dbResult), TimeUnit.MILLISECONDS.convert(expired, timeUnit));
		return dbResult;
	}
	public <K, V> Map<K, V> getMap(String cacheKey, Supplier<Map<K, V>> dbFallback)
	{
		return getMap(cacheKey, dbFallback, 12, TimeUnit.HOURS);
	}
}