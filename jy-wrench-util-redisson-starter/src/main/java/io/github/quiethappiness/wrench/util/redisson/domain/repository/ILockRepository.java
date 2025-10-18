package io.github.quiethappiness.wrench.util.redisson.domain.repository;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ILockRepository
{
	<T> T lockAndGet(String lockkey, Supplier<T> supplier);
	
	<T> void lockAndRun(String lockkey, Consumer<T> supplier, T t);
}
