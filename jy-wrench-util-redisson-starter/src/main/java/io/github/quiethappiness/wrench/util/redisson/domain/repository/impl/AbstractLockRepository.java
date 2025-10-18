package io.github.quiethappiness.wrench.util.redisson.domain.repository.impl;

import io.github.quiethappiness.wrench.util.redisson.domain.base.inter.IRedisThread;
import io.github.quiethappiness.wrench.util.redisson.domain.repository.ILockRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public abstract class AbstractLockRepository implements ILockRepository
{
	@Resource
	private IRedisThread redisThread;
	
	@Override
	public <T> T lockAndGet(String lockkey, Supplier<T> supplier)
	{
		// 假设是多实例运行，则使用分布式锁防止重复执行
		RLock lock = redisThread.getLock(lockkey);
		try
		{
			boolean isLocked = lock.tryLock(3, 0, TimeUnit.SECONDS);
			if (!isLocked)
			{
				return supplier.get();
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
	public <T> void lockAndRun(String lockkey, Consumer<T> supplier, T t)
	{
		// 假设是多实例运行，则使用分布式锁防止重复执行
		RLock lock = redisThread.getLock(lockkey);
		try
		{
			boolean isLocked = lock.tryLock(3, 0, TimeUnit.SECONDS);
			if (!isLocked)
			{
				supplier.accept(t);
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
