package io.github.quiethappiness.wrench.util.redisson.domain.service;

import org.aspectj.lang.annotation.Pointcut;

/**
 * ILockAndDoAOP
 * @description 针对lock注解的aop
 * @author quietHappiness @jingyue
 * @date 2025/11/5 11:12
 * @version 1.0
 */
public interface ILockAndDoAOP
{
	@Pointcut("@annotation(io.github.quiethappiness.wrench.util.redisson.types.annotations.LockAndGet)")
	default void lockAndRunPointcut()
	{
	}
	
	String JY_UTIL_REDISSON_LOCK_AND_DO = "JY:util:redisson:lockAndDo";
}