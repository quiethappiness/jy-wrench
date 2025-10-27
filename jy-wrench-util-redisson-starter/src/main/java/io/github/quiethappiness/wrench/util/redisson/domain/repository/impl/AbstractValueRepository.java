package io.github.quiethappiness.wrench.util.redisson.domain.repository.impl;

import io.github.quiethappiness.wrench.util.redisson.domain.base.impl.IRedisService;
import io.github.quiethappiness.wrench.util.redisson.domain.execption.CacheAccessException;
import io.github.quiethappiness.wrench.util.redisson.domain.repository.IValueRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractValueRepository implements IValueRepository
{
	@Resource
	protected IRedisService redisService;
	protected static final String NULL = "NULL";
	
	/**
	 * 通用缓存处理方法
	 * 优先从缓存获取，缓存不存在则从数据库获取并写入缓存
	 * @param rate
	 * 	偏移概率，范围[0,1)，如果是01，则为正负10%的随机过期时间
	 * @param cacheKey
	 * 	缓存键
	 * @param dbFallback
	 * 	数据库查询函数
	 * @param <T>
	 * 	返回类型
	 * @return 查询结果
	 */
	@Override
	public <T, R> Optional<R> cacheOne(
		String cacheKey,
		Supplier<T> dbFallback,
		Function<T, R> mapper,
		long expired,
		TimeUnit timeUnit, double rate)
	{
		try
		{
			// 第一次缓存检查
			Object cached = redisService.getValue(cacheKey);
			if (cached != null)
			{
				if (isNullValue(cached))
				{
					return Optional.empty();
				}
				return Optional.of((R) cached);
			}
			// 获取分布式锁
			String lockKey = "lock:" + cacheKey;
			RLock lock = redisService.getLock(lockKey);
			try
			{
				// 合理的等待时间
				if (lock.tryLock(200, 3000, TimeUnit.MILLISECONDS))
				{
					// 第二次缓存检查（双重检查）
					Object secondCheck = redisService.getValue(cacheKey);
					if (secondCheck != null)
					{
						if (isNullValue(secondCheck))
						{
							return Optional.empty();
						}
						return Optional.of((R) secondCheck);
					}
					// 查询数据库
					T dbResult = dbFallback.get();
					if (dbResult == null)
					{
						// 同步缓存空值
						setNULLToCache(cacheKey);
						return Optional.empty();
					}
					else
					{
						R result = mapper.apply(dbResult);
						long randomExpire = generateRandomExpiredTime(expired, timeUnit, rate);
						redisService.setValue(cacheKey, result, randomExpire);
						return Optional.of(result);
					}
				}
				else
				{
					log.warn("获取分布式锁超时, key: {}", lockKey);
					// 可以返回空或者抛出业务异常
					return Optional.empty();
				}
			}
			finally
			{
				if (lock.isHeldByCurrentThread())
				{
					lock.unlock();
				}
			}
		}
		catch (InterruptedException e)
		{
			Thread.currentThread()
				.interrupt();
			throw new CacheAccessException("操作被中断", e);
		}
		catch (Exception e)
		{
			log.error("缓存处理异常, key: {}", cacheKey, e);
			throw new CacheAccessException("缓存处理失败", e);
		}
	}
	
	private static long generateRandomExpiredTime(long expired, TimeUnit timeUnit, double rate)
	{
		// 同步缓存实际值
		long convert = TimeUnit.MILLISECONDS.convert(expired, timeUnit);
		// 添加随机过期时间防止缓存雪崩，范围±10%
		return (long) (convert + (long) (Math.random() * convert * rate * 2) - (convert * rate));
	}
	
	// 辅助方法
	private boolean isNullValue(Object value)
	{
		return NULL.equals(value);
	}
	
	/**
	 * 通用缓存列表处理方法
	 * 优先从缓存获取，缓存不存在则从数据库获取并写入缓存，防止缓存击穿
	 * @param cacheKey
	 * 	缓存键
	 * @param dbFallback
	 * 	数据库查询函数
	 * @param mapper
	 * 	数据转换函数
	 * @param expired
	 * 	过期时间
	 * @param timeUnit
	 * 	时间单位
	 * @return Optional包装的查询结果列表，Optional.empty()表示数据不存在
	 */
	@Override
	public <T, R> Optional<List<R>> cacheList(
		String cacheKey,
		Supplier<List<T>> dbFallback,
		Function<T, R> mapper,
		long expired,
		TimeUnit timeUnit,
		double rate)
	{
		// 第一次从缓存获取
		Object value = redisService.getValue(cacheKey);
		log.debug("第1次从缓存获取结果：{}", value);
		if (value != null)
		{
			// 处理空值标记
			if (isNullValue(value))
			{
				return Optional.empty();
			}
			// 处理列表数据
			if (value instanceof List)
			{
				try
				{
					return Optional.of((List<R>) value);
				}
				catch (ClassCastException e)
				{
					log.warn("缓存值类型转换异常，key: {}，期望类型: List，实际类型: {}",
						cacheKey, value.getClass()
							.getSimpleName(), e);
					// 类型转换失败，删除异常缓存，继续查询数据库
					redisService.remove(cacheKey);
				}
			}
			else
			{
				log.warn("缓存值类型异常，key: {}，期望List，实际：{}",
					cacheKey, value.getClass()
						.getSimpleName());
				// 删除异常缓存，继续查询数据库
				redisService.remove(cacheKey);
			}
		}
		// 使用分布式锁防止缓存击穿
		String lockKey = "lock:" + cacheKey;
		RLock lock = redisService.getLock(lockKey);
		try
		{
			// 尝试获取锁，设置合理的等待时间
			boolean isLocked = lock.tryLock(200, 3000, TimeUnit.MILLISECONDS);
			if (!isLocked)
			{
				log.warn("获取分布式锁失败，key: {}", lockKey);
				// 返回Optional.empty()表示数据暂时不可用
				return Optional.empty();
			}
			try
			{
				// 第二次从缓存获取（双重检查）
				Object value2 = redisService.getValue(cacheKey);
				log.debug("第2次从缓存获取结果：{}", value2);
				if (value2 != null)
				{
					if (isNullValue(value2))
					{
						return Optional.empty();
					}
					if (value2 instanceof List)
					{
						try
						{
							return Optional.of((List<R>) value2);
						}
						catch (ClassCastException e)
						{
							log.warn("缓存值类型转换异常（第二次检查），key: {}，删除异常缓存", cacheKey, e);
							redisService.remove(cacheKey);
						}
					}
				}
				// 缓存不存在，查询数据库
				List<T> dbResult = dbFallback.get();
				if (CollectionUtils.isEmpty(dbResult))
				{
					// 数据库结果为空，缓存空值标记
					setNULLToCache(cacheKey);
					return Optional.empty();
				}
				else
				{
					// 转换并缓存结果
					List<R> resultList = dbResult.stream()
						.map(mapper)
						.collect(Collectors.toList());
					// 写入缓存（建议同步写入确保一致性）
					// 使用ArrayList确保序列化兼容性
					redisService.setValue(cacheKey, new ArrayList<>(resultList),
						generateRandomExpiredTime(expired, timeUnit, rate));
					return Optional.of(resultList);
				}
			}
			finally
			{
				// 确保释放锁
				if (lock.isLocked() && lock.isHeldByCurrentThread())
				{
					lock.unlock();
				}
			}
		}
		catch (InterruptedException e)
		{
			// 恢复中断状态
			Thread.currentThread()
				.interrupt();
			log.warn("获取缓存操作被中断，key: {}", cacheKey, e);
			return Optional.empty();
		}
		catch (Exception e)
		{
			log.error("处理缓存列表时发生异常，key: {}", cacheKey, e);
			return Optional.empty();
		}
	}
	
	/**
	 * 通用缓存Map处理方法
	 * 优先从缓存获取，缓存不存在则从数据库获取并写入缓存，防止缓存击穿
	 * @param cacheKey
	 * 	缓存键
	 * @param dbFallback
	 * 	数据库查询函数
	 * @param expired
	 * 	过期时间
	 * @param timeUnit
	 * 	时间单位
	 * @return Optional包装的查询结果Map，Optional.empty()表示数据不存在
	 */
	@Override
	public <K, V> Optional<Map<K, V>> cacheMap(
		String cacheKey,
		Supplier<Map<K, V>> dbFallback,
		long expired,
		TimeUnit timeUnit,
		double rate)
	{
		// 第一次从缓存获取
		Object value = redisService.getValue(cacheKey);
		log.debug("第1次从缓存获取Map结果：{}", value);
		if (value != null)
		{
			// 处理空值标记
			if (isNullValue(value))
			{
				return Optional.empty();
			}
			// 处理Map数据
			if (value instanceof Map)
			{
				try
				{
					return Optional.of((Map<K, V>) value);
				}
				catch (ClassCastException e)
				{
					log.warn("缓存Map值类型转换异常，key: {}，期望类型: Map，实际类型: {}",
						cacheKey, value.getClass()
							.getSimpleName(), e);
					// 类型转换失败，删除异常缓存，继续查询数据库
					redisService.remove(cacheKey);
				}
			}
			else
			{
				log.warn("缓存Map值类型异常，key: {}，期望Map，实际：{}",
					cacheKey, value.getClass()
						.getSimpleName());
				// 删除异常缓存，继续查询数据库
				redisService.remove(cacheKey);
			}
		}
		// 使用分布式锁防止缓存击穿
		String lockKey = "lock:" + cacheKey;
		RLock lock = redisService.getLock(lockKey);
		try
		{
			// 尝试获取锁，设置合理的等待时间
			boolean isLocked = lock.tryLock(200, 3000, TimeUnit.MILLISECONDS);
			if (!isLocked)
			{
				log.warn("获取分布式锁失败，key: {}", lockKey);
				return Optional.empty();
			}
			try
			{
				// 第二次从缓存获取（双重检查）
				Object value2 = redisService.getValue(cacheKey);
				log.debug("第2次从缓存获取Map结果：{}", value2);
				if (value2 != null)
				{
					if (isNullValue(value2))
					{
						return Optional.empty();
					}
					if (value2 instanceof Map)
					{
						try
						{
							return Optional.of((Map<K, V>) value2);
						}
						catch (ClassCastException e)
						{
							log.warn("缓存Map值类型转换异常（第二次检查），key: {}，删除异常缓存", cacheKey, e);
							redisService.remove(cacheKey);
						}
					}
				}
				// 缓存不存在，查询数据库
				Map<K, V> dbResult = dbFallback.get();
				if (CollectionUtils.isEmpty(dbResult))
				{
					// 数据库结果为空，缓存空值标记
					setNULLToCache(cacheKey);
					return Optional.empty();
				}
				else
				{
					// 使用新HashMap确保序列化兼容性
					Map<K, V> cacheMap = new HashMap<>(dbResult);
					// 写入缓存（建议同步写入确保一致性）
					redisService.setValue(cacheKey, cacheMap,
						generateRandomExpiredTime(expired, timeUnit, rate));
					return Optional.of(dbResult); // 返回原始数据，避免重复创建对象
				}
			}
			finally
			{
				// 确保释放锁
				if (lock.isLocked() && lock.isHeldByCurrentThread())
				{
					lock.unlock();
				}
			}
		}
		catch (InterruptedException e)
		{
			// 恢复中断状态
			Thread.currentThread()
				.interrupt();
			log.warn("获取Map缓存操作被中断，key: {}", cacheKey, e);
			return Optional.empty();
		}
		catch (Exception e)
		{
			log.error("处理缓存Map时发生异常，key: {}", cacheKey, e);
			return Optional.empty();
		}
	}
	
	protected void setNULLToCache(String cacheKey)
	{
		redisService.setValue(cacheKey, NULL, TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES));
	}
}
