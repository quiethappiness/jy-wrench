package io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.strategy;

import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcIdempotent;
import io.github.quiethappiness.wrench.util.types.common.util.StringCaseUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

import static io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.getTargetMethodFromJP;

/**
 * IdempotentStrategy
 * @description 幂等性保证接口
 * @author quietHappiness @jingyue
 * @date 2025/11/3 17:18
 * @version 1.0
 */
public interface IdempotentStrategy
{
	 static String spliceBusinessType(ProceedingJoinPoint joinPoint) throws NoSuchMethodException
	{
		Method method = getTargetMethodFromJP(joinPoint);
		return method
			.getDeclaringClass()
			.getSimpleName() + "." + method.getName();
	}
	
	Object tokenCheck(ProceedingJoinPoint joinPoint, TcIdempotent idempotent, String token) throws NoSuchMethodException;
	
	void frequencyCheck(ProceedingJoinPoint joinPoint, TcIdempotent idempotent, String token) throws NoSuchMethodException;
	
	void similarCheck(ProceedingJoinPoint joinPoint, String token) throws NoSuchMethodException;
	
	Object execJoinPoint(ProceedingJoinPoint joinPoint, String token) throws Throwable;
	
	@AllArgsConstructor
	@Getter
	enum IdempotentStrategyEnum
	{
		FAST("fast", "快速幂等性实现", StringCaseUtils.firstLetterToLowerCase(FastLevelIdempotentStrategy.class.getSimpleName()), TcIdempotent.Level.FAST),
		NORMAL("normal", "正常幂等性实现", StringCaseUtils.firstLetterToLowerCase(NormalLevelIdempotentStrategy.class.getSimpleName()), TcIdempotent.Level.NORMAL),
		STRICT("strict", "严格幂等性实现", StringCaseUtils.firstLetterToLowerCase(StrictLevelIdempotentStrategy.class.getSimpleName()), TcIdempotent.Level.STRICT)
		;
		private final String code;
		private final String info;
		private final String beanName;
		private final TcIdempotent.Level map;
		
		public static IdempotentStrategyEnum getByCode(String code)
		{
			for (IdempotentStrategyEnum value : IdempotentStrategyEnum.values())
			{
				if (value.code.equals(code))
				{
					return value;
				}
			}
			throw new IllegalArgumentException("Invalid code: " + code);
		}
		
		public static IdempotentStrategyEnum getByMap(TcIdempotent.Level map)
		{
			for (IdempotentStrategyEnum value : IdempotentStrategyEnum.values())
			{
				if (value.map.equals(map))
				{
					return value;
				}
			}
			throw new IllegalArgumentException("Invalid map: " + map);
		}
	}
}