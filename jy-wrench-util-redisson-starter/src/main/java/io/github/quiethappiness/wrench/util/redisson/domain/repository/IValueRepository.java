package io.github.quiethappiness.wrench.util.redisson.domain.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IValueRepository
{
	<T, R> Optional<R> cacheOne(
		String cacheKey,
		Supplier<T> dbFallback,
		Function<T, R> mapper,
		long expired,
		TimeUnit timeUnit);
	<T, R> Optional<R> cacheOne(
		String cacheKey,
		Supplier<T> dbFallback,
		Function<T, R> mapper);
	
	<T, R> Optional<List<R>> cacheList(
		String cacheKey,
		Supplier<List<T>> dbFallback,
		Function<T, R> mapper,
		long expired,
		TimeUnit timeUnit);
	<T, R> Optional<List<R>> cacheList(
		String cacheKey,
		Supplier<List<T>> dbFallback,
		Function<T, R> mapper);
	
	<K, V> Optional<Map<K, V>> cacheMap(
		String cacheKey,
		Supplier<Map<K, V>> dbFallback,
		long expired,
		TimeUnit timeUnit);
	<K, V> Optional<Map<K, V>> cacheMap(
		String cacheKey,
		Supplier<Map<K, V>> dbFallback);
}
