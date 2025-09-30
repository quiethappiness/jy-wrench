
package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist;

import io.github.quiethappiness.wrench.traffic.control.domain.model.valobj.TrafficControlContext;
import io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.RateLimiterAOP;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.WhiteListChecker;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@ConditionalOnBean(RateLimiterAOP.class)
@Order(1)
@Slf4j
public class WhiteListAOP extends AbstractWhiteListAop
{
	@Around(value = "whiteListCheckerWithRateLimiter() &&@annotation(whiteListChecker)  ", argNames = "jp,whiteListChecker")
	public Object doWhitelistCheck(ProceedingJoinPoint jp, WhiteListChecker whiteListChecker) throws Throwable
	{
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes == null)
		{
			log.error("Request attributes not found, cannot proceed with whitelist check");
			return jp.proceed();
		}
		// 解析key表达式获取userId
		Result result = doCheck(jp, attributes, whiteListChecker);
		if (result.isInWhitelist)
		{
			log.info("User {} is in whitelist, proceeding normally without rate limiting", result.userId);
			// 用户在白名单中，直接放行，不触发限流
		}
		else
		{
			log.info("User {} is not in whitelist, will check rate limit", result.userId);
			// 用户不在白名单中，添加一个标记然后继续执行
			// 这里我们使用ThreadLocal来传递状态
		}
		TrafficControlContext.setInWhiteList(result.isInWhitelist);
		try
		{
			return jp.proceed();
		}
		finally
		{
			TrafficControlContext.clear();
		}
	}
	
	@Around(value = "whiteListCheckerWithoutRateLimiter() && @annotation(whiteListChecker) ", argNames = "jp,whiteListChecker")
	public Object doWhitelistCheckWithoutRateLimiter(ProceedingJoinPoint jp, WhiteListChecker whiteListChecker) throws Throwable
	{
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes == null)
		{
			log.error("Request attributes not found, cannot proceed with whitelist check");
			return jp.proceed();
		}
		// 解析key表达式获取userId
		Result result = doCheck(jp, attributes, whiteListChecker);
		if (result.isInWhitelist)
		{
			log.info("User {} is in whitelist, proceeding normally", result.userId);
			// 用户在白名单中，直接放行
			return jp.proceed();
		}
		else
		{
			log.info("User {} is not in whitelist, access denied", result.userId);
			// 用户不在白名单中，抛出异常或返回错误
			throw new IllegalAccessException("User not in whitelist");
		}
	}
}
