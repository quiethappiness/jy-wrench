package io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.strategy.impl;

import io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.FieldPart;
import io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.strategy.AbstractIdempotentStrategy;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcIdempotent;
import io.github.quiethappiness.wrench.traffic.control.types.exception.IdempotentException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Service;

/**
 * FastLevelIdempotentStrategy
 * @description 常用层级的幂等性保证
 * @author quietHappiness @jingyue
 * @date 2025/11/3 17:17
 * @version 1.0
 */
@Service
@Slf4j
public class NormalLevelIdempotentStrategy extends AbstractIdempotentStrategy
{
	
	@Override
	public void frequencyCheck(ProceedingJoinPoint joinPoint, TcIdempotent idempotent, String token) throws NoSuchMethodException
	{
		String expression = idempotent.rateMark();
		String expressionValue = FieldPart.extractSpelExpressionValue(joinPoint, expression, String.class);
		String businessType = spliceBusinessType(joinPoint);
		if (idempotentCheck.isRequestTooFrequent(businessType, idempotent,expressionValue))
		{
			// 释放令牌，允许重试
			idempotentToken.preReleaseToken(token);
			throw new IdempotentException("操作过于频繁，请稍后再试");
		}
	}
}