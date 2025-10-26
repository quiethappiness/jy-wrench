package io.github.quiethappiness.wrench.util.redisson.domain.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author jingyue @quiethappiness
 * @description 仓储抽象类
 */
@Slf4j
@Repository
public class ValueRepository extends AbstractValueRepository
{
	
	@Override
	public <T, R> Optional<R> cacheOne(String cacheKey, Supplier<T> dbFallback, Function<T, R> mapper)
	{
		return cacheOne(cacheKey, dbFallback, mapper, 6, TimeUnit.HOURS, 0.1);
	}
	
	@Override
	public <T, R> Optional<R> cacheOne(
		String cacheKey, Supplier<T> dbFallback, Function<T, R> mapper, long expired, TimeUnit timeUnit)
	{
		return cacheOne(cacheKey, dbFallback, mapper, expired, timeUnit, 0.1);
	}
	
	@Override
	public <T, R> Optional<List<R>> cacheList(String cacheKey, Supplier<List<T>> dbFallback, Function<T, R> mapper)
	{
		return cacheList(cacheKey, dbFallback, mapper, 6, TimeUnit.HOURS, 0.1);
	}
	
	@Override
	public <T, R> Optional<List<R>> cacheList(String cacheKey, Supplier<List<T>> dbFallback, Function<T, R> mapper, long expired, TimeUnit timeUnit)
	{
		return cacheList(cacheKey, dbFallback, mapper, expired, timeUnit, 0.1);
	}
	
	@Override
	public <K, V> Optional<Map<K, V>> cacheMap(String cacheKey, Supplier<Map<K, V>> dbFallback)
	{
		return cacheMap(cacheKey, dbFallback, 6, TimeUnit.HOURS, 0.1);
	}
	
	@Override
	public <K, V> Optional<Map<K, V>> cacheMap(String cacheKey, Supplier<Map<K, V>> dbFallback, long expired, TimeUnit timeUnit)
	{
		return cacheMap(cacheKey, dbFallback, expired, timeUnit, 0.1);
	}
}