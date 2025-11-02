package io.github.quiethappiness.wrench.traffic.control.domain.service.hystrix;

import io.github.quiethappiness.wrench.traffic.control.domain.service.IValueHystrixAOP;
import io.github.quiethappiness.wrench.traffic.control.domain.service.hystrix.business.IValveHystrixService;
import io.github.quiethappiness.wrench.traffic.control.domain.service.hystrix.business.ValueHystrixServiceImpl;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcHystrix;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import static io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.getTargetMethodFromJP;

@Service
@Slf4j
@Aspect
@Order(3)
public class ValueHystrixAOP implements IValueHystrixAOP
{
	
	@Around("hystrixPointcut() && @annotation(tcHystrix)")
	public Object doAccessRateLimiter(ProceedingJoinPoint jp, TcHystrix tcHystrix) throws Throwable
	{
		// 配置超时时间
		IValveHystrixService valueHystrixService = new ValueHystrixServiceImpl(tcHystrix.timeout());
		return valueHystrixService.access(jp, getTargetMethodFromJP(jp), tcHystrix, jp.getArgs());
	}
}
