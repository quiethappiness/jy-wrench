package io.github.quiethappiness.wrench.util.redisson.domain.base.func;

import org.redisson.api.*;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface IRedisThread
{
	
	Boolean setNx(String key);
	
	Boolean setNx(String key, long expired, TimeUnit timeUnit);
	
	Boolean setNx(String key, Duration expired);
	/**
	 * 获取 Redis 锁（可重入锁）
	 * @param key
	 * 	键
	 * @return Lock
	 */
	RLock getLock(String key);
	
	/**
	 * 获取 Redis 锁（公平锁）
	 * @param key
	 * 	键
	 * @return Lock
	 */
	RLock getFairLock(String key);
	
	/**
	 * 获取 Redis 锁（读写锁）
	 * @param key
	 * 	键
	 * @return RReadWriteLock
	 */
	RReadWriteLock getReadWriteLock(String key);
	
	/**
	 * 获取 Redis 信号量
	 * @param key
	 * 	键
	 * @return RSemaphore
	 */
	RSemaphore getSemaphore(String key);
	
	/**
	 * 获取 Redis 过期信号量
	 * <p>
	 * 基于Redis的Redisson的分布式信号量（Semaphore）Java对象RSemaphore采用了与java.util.concurrent.Semaphore相似的接口和用法。
	 * 同时还提供了异步（Async）、反射式（Reactive）和RxJava2标准的接口。
	 * @param key
	 * 	键
	 * @return RPermitExpirableSemaphore
	 */
	RPermitExpirableSemaphore getPermitExpirableSemaphore(String key);
	
	/**
	 * 闭锁
	 * @param key
	 * 	键
	 * @return RCountDownLatch
	 */
	RCountDownLatch getCountDownLatch(String key);
}