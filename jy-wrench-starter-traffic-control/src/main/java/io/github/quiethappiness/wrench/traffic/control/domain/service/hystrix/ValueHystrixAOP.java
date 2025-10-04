package io.github.quiethappiness.wrench.traffic.control.domain.service.hystrix;

import io.github.quiethappiness.wrench.traffic.control.domain.service.IValueHystrixAOP;
import io.github.quiethappiness.wrench.traffic.control.domain.service.hystrix.business.IValveHystrixService;
import io.github.quiethappiness.wrench.traffic.control.domain.service.hystrix.business.ValueHystrixServiceImpl;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcHystrix;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;

import static io.github.quiethappiness.aop.util.WrenchAopUtil.getTargetMethodFromJP;

@Service
@Slf4j
@Aspect
public class ValueHystrixAOP implements IValueHystrixAOP
{
	
	@Around("hystrixPointcut() && @annotation(tcValueHy)")
	public Object doAccessRateLimiter(ProceedingJoinPoint jp, TcHystrix tcValueHy) throws Throwable
	{
		IValveHystrixService valueHystrixService = new ValueHystrixServiceImpl(tcValueHy.timeout());
		return valueHystrixService.access(jp, getTargetMethodFromJP(jp), tcValueHy, jp.getArgs());
	}
}
