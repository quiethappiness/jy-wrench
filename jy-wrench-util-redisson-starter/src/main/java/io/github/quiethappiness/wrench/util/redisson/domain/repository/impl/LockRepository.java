package io.github.quiethappiness.wrench.util.redisson.domain.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Repository
@Validated
public class LockRepository extends AbstractLockRepository
{
	@Override
	public <T> T lockAndGet(String lockkey, Supplier<T> supplier)
	{
		return lockAndGet(lockkey, supplier, 3, 0, TimeUnit.SECONDS);
	}
	@Override
	public <T> T lockAndGet(String lockkey, Supplier<T> supplier, long waitTime, long leaseTime, TimeUnit unit)
	{
		return lockAndGet("RLock",lockkey, supplier, waitTime, leaseTime, unit);
	}
	@Override
	public <T> void lockAndRun(String lockkey, Consumer<T> supplier, T t)
	{
		lockAndRun(lockkey, supplier, t, 3, 0, TimeUnit.SECONDS);
	}
}