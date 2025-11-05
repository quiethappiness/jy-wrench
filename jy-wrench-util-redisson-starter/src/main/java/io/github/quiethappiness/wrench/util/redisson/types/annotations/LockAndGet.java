package io.github.quiethappiness.wrench.util.redisson.types.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * LockAndGet
 * @description 加个Rlock锁
 * @author quietHappiness @jingyue
 * @date 2025/11/5 11:07
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface LockAndGet
{
	String lockKey();
	long waitTime() default 3;
	long leaseTime() default 0;
	TimeUnit unit() default TimeUnit.SECONDS;
	
}