package io.github.quiethappiness.wrench.util.redisson.domain.service;

import io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.FieldPart;
import io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.MethodPart;
import io.github.quiethappiness.wrench.util.redisson.domain.repository.ILockRepository;
import io.github.quiethappiness.wrench.util.redisson.types.annotations.LockAndGet;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * LockAndDoAOP
 * @description 针对lock注解的aop实现
 * @author quietHappiness @jingyue
 * @date 2025/11/5 11:24
 * @version 1.0
 */
@Slf4j
@Service
@Aspect
@Order(2)
public class LockAndDoAOP implements ILockAndDoAOP
{
	@Resource
	private ILockRepository lockRepository;
	
	@Around("lockAndRunPointcut() && @annotation(lockAndGet)")
	public Object doAccessLockAndGet(ProceedingJoinPoint jp, LockAndGet lockAndGet) throws Throwable
	{
		Method method = MethodPart.getTargetMethodFromJP(jp);
		log.warn("LockAndGetAOP:{}",method.getName());
		String lockKey = lockAndGet.lockKey();
		if (!StringUtils.hasText(lockKey))
		{
			throw new RuntimeException("lockKey is null");
		}
		String expressionValue = FieldPart.extractSpelExpressionValue(jp, lockKey, String.class);
		return lockRepository.lockAndGet(expressionValue, () ->
		{
			try
			{
				return jp.proceed();
			}
			catch (Throwable e)
			{
				throw new RuntimeException(e);
			}
		}, lockAndGet.waitTime(), lockAndGet.leaseTime(), lockAndGet.unit());
	}
}