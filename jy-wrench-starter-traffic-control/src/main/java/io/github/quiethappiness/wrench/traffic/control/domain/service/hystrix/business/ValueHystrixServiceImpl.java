package io.github.quiethappiness.wrench.traffic.control.domain.service.hystrix.business;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.*;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcHystrix;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

import static io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.fallbackMethodResult;

@Slf4j
public class ValueHystrixServiceImpl extends HystrixCommand<Object> implements IValveHystrixService
{
	private ProceedingJoinPoint jp;
	private Method method;
	private TcHystrix doHystrix;
	
	/*
	 *置HystrixCommand的属性
	 *GroupKey：该命令属于哪一个组，可以帮助我们更好的组织命令。
	 *CommandKey：该命令的名称
	 *ThreadPoolKey：该命令所属线程池的名称，同样配置的命令会共享同一线程池，若不配置，会默认使用GroupKey作为线程池名称。
	 *CommandProperties：该命令的一些设置，包括断路器的配置，隔离策略，降级设置，以及一些监控指标等。
	 *ThreadPoolProperties：关于线程池的配置，包括线程池大小，排队队列的大小等
	 */
	public ValueHystrixServiceImpl(int timeout)
	{
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GovernGroup"))
			.andCommandKey(HystrixCommandKey.Factory.asKey("GovernKey"))
			.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GovernThreadPool"))
			.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
				.withExecutionTimeoutInMilliseconds(timeout)
				.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
				.withCircuitBreakerEnabled(true)
			)
			.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
				.withCoreSize(10))
		);
	}
	
	@Override
	public Object access(ProceedingJoinPoint jp, Method method, TcHystrix doHystrix, Object[] args)
	{
		this.jp = jp;
		this.method = method;
		this.doHystrix = doHystrix;
		//设置熔断超时时间
		// Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GovernGroup"))
		// 	.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
		// 		.withExecutionTimeoutInMilliseconds(doHystrix.timeout()));
		return this.execute();
	}
	
	@Override
	protected Object run() throws Exception
	{
		try
		{
			return jp.proceed();
		}
		catch (Throwable throwable)
		{
			return null;
		}
	}
	
	@Override
	protected Object getFallback()
	{
		String returnJson = doHystrix.returnJson();
		if (StringUtils.hasText(returnJson))
		{
			try
			{
				return JSON.parseObject(returnJson, method.getReturnType());
			}
			catch (Exception e)
			{
				log.error("Error while parsing fallback json", e);
			}
		}
		try
		{
			return fallbackMethodResult(jp, doHystrix.fallbackMethod());
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
