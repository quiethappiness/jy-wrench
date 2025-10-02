package io.github.quiethappiness.wrench.traffic.control.domain.service;

import org.aspectj.lang.annotation.Pointcut;

public interface IValueHystrixAOP
{
	@Pointcut("@annotation(io.github.quiethappiness.wrench.traffic.control.types.annotations.TcHystrix)")
	default void hystrixPointcut()
	{
	}
}
