package io.github.quiethappiness.wrench.util.redisson.domain.repository.impl;

import io.github.quiethappiness.wrench.util.redisson.domain.base.impl.IRedisService;
import io.github.quiethappiness.wrench.util.redisson.domain.repository.ILockRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Validated
public abstract class AbstractLockRepository implements ILockRepository
{
	@Resource
	protected IRedisService redisService;
	
	@Override
	public <T> T lockAndGet(String prefix, String lockkey, Supplier<T> supplier, long waitTime, long leaseTime, TimeUnit unit)
	{
		// 假设是多实例运行，则使用分布式锁防止重复执行
		RLock lock = redisService.getLock(prefix + lockkey);
		try
		{
			boolean isLocked = lock.tryLock(waitTime, leaseTime, unit);
			if (isLocked)
			{
				return supplier.get();
			}
			else
			{
				log.warn("Lock is already locked, skip execution");
			}
		}
		catch (InterruptedException e)
		{
			// 如果是中断异常，需要恢复中断状态
			Thread.currentThread()
			      .interrupt();
		}
		catch (Exception e)
		{
			log.error("Lock error", e);
		}
		finally
		{
			// 释放分布式锁
			if (lock.isLocked() && lock.isHeldByCurrentThread())
			{
				lock.unlock();
			}
		}
		return null;
	}
	
	@Override
	public <T> void lockAndRun(String lockkey, Consumer<T> supplier, T t, long waitTime, long leaseTime, TimeUnit unit)
	{
		// 假设是多实例运行，则使用分布式锁防止重复执行
		RLock lock = redisService.getLock("RLock:" + lockkey);
		try
		{
			boolean isLocked = lock.tryLock(waitTime, leaseTime, unit);
			if (isLocked)
			{
				supplier.accept(t);
			}
			else
			{
				log.warn("Lock is already locked, skip execution");
			}
		}
		catch (InterruptedException e)
		{
			// 如果是中断异常，需要恢复中断状态
			Thread.currentThread()
			      .interrupt();
		}
		catch (Exception e)
		{
			log.error("Lock error", e);
		}
		finally
		{
			// 释放分布式锁
			if (lock.isLocked() && lock.isHeldByCurrentThread())
			{
				lock.unlock();
			}
		}
	}
}