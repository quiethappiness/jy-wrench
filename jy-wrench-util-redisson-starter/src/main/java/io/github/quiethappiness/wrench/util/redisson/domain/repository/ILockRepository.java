package io.github.quiethappiness.wrench.util.redisson.domain.repository;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ILockRepository
{
	<T> T lockAndGet(String lockkey, Supplier<T> supplier);
	<T> T lockAndGet(String lockkey, Supplier<T> supplier,long waitTime, long leaseTime, TimeUnit unit);
	<T> T lockAndGet(String prefix,String lockkey, Supplier<T> supplier,long waitTime, long leaseTime, TimeUnit unit);
	
	<T> void lockAndRun(String lockkey, Consumer<T> supplier, T t);
	<T> void lockAndRun(String lockkey, Consumer<T> supplier, T t,long waitTime, long leaseTime, TimeUnit unit);
}