
package io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist;

import io.github.quiethappiness.wrench.traffic.control.config.property.WhiteListProperties;
import io.github.quiethappiness.wrench.traffic.control.domain.model.entity.WhiteListResultEntity;
import io.github.quiethappiness.wrench.traffic.control.domain.model.valobj.TrafficControlContext;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.AbstractWhiteListSupport;
import io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist.tree.factory.WhiteListStrategyFactory;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcWhiteList;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.fallbackMethodResult;

@Aspect
@Component
// @ConditionalOnBean(RateLimiterAOP.class)
@Order(1)
@Slf4j
public class WhiteListAOP extends AbstractWhiteListAOP
{
	public WhiteListAOP(WhiteListProperties whiteListProperties, WhiteListStrategyFactory whiteListStrategyFactory)
	{
		super(whiteListProperties, whiteListStrategyFactory);
	}
	
	@Around(value = "whiteListCheckerWithRateLimiter() &&@annotation(tcWhiteList)  ", argNames = "jp,tcWhiteList")
	public Object doWhitelistCheck(ProceedingJoinPoint jp, TcWhiteList tcWhiteList) throws Throwable
	{
		// 解析key表达式获取userId
		WhiteListResultEntity resultEntity = doCheck(jp, tcWhiteList);
		AbstractWhiteListSupport.InWhitListResult inWhitListResult = resultEntity.getInWhitListResult();
		if (inWhitListResult == null || inWhitListResult.userId() == null)
		{
			return jp.proceed();
		}
		if (inWhitListResult.isInWhitelist())
		{
			log.info("User {} is in whitelist, proceeding normally without rate limiting", inWhitListResult.userId());
			// 用户在白名单中，直接放行，不触发限流
		}
		else
		{
			log.info("User {} is not in whitelist, will check rate limit", inWhitListResult.userId());
			// 用户不在白名单中，添加一个标记然后继续执行
			// 这里我们使用ThreadLocal来传递状态
		}
		TrafficControlContext.setInWhiteList(inWhitListResult.isInWhitelist());
		try
		{
			return jp.proceed();
		}
		finally
		{
			TrafficControlContext.clear();
		}
	}
	
	@Around(value = "whiteListCheckerWithoutRateLimiter() && @annotation(tcWhiteList) ", argNames = "jp,tcWhiteList")
	public Object doWhitelistCheckWithoutRateLimiter(ProceedingJoinPoint jp, TcWhiteList tcWhiteList) throws Throwable
	{
		// 解析key表达式获取userId
		WhiteListResultEntity resultEntity = doCheck(jp, tcWhiteList);
		AbstractWhiteListSupport.InWhitListResult inWhitListResult = resultEntity.getInWhitListResult();
		if (inWhitListResult == null || inWhitListResult.userId() == null)
		{
			return jp.proceed();
		}
		if (inWhitListResult.isInWhitelist())
		{
			log.info("User {} is in whitelist, proceeding normally", inWhitListResult.userId());
			// 用户在白名单中，直接放行
			return jp.proceed();
		}
		else
		{
			log.info("User {} is not in whitelist, access denied", inWhitListResult.userId());
			// 用户不在白名单中，抛出异常或返回错误
			log.error("User not in whitelist");
			return fallbackMethodResult(jp, tcWhiteList.fallbackMethod());
		}
	}
}
