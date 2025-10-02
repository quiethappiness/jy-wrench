package io.github.quiethappiness.wrench.traffic.control.domain.service.hystrix.business;

import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcHystrix;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

public interface IValveHystrixService
{
	Object access(ProceedingJoinPoint jp, Method method, TcHystrix doHystrix, Object[] args);
}
