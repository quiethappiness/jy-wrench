package io.github.quiethappiness.wrench.db.router.domain.service;

import io.github.quiethappiness.wrench.aop.util.WrenchAopUtil;
import io.github.quiethappiness.wrench.db.router.domain.model.DBContextHolder;
import io.github.quiethappiness.wrench.db.router.types.annotations.DBRouter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Aspect
@Component("db-router-aop")
@Slf4j
public class DBRouterAOP extends AbstractDBRouterAOP
{
	
	@Around("aopPoint() && @annotation(dbRouter)")
	public Object doRouter(ProceedingJoinPoint jp, DBRouter dbRouter) throws Throwable
	{
		String dbKey = dbRouter.field();
		if (!StringUtils.hasText(dbKey))
		{
			throw new RuntimeException("annotation DBRouter key is null！");
		}
		// 计算路由
		CalRouterResult result = doCalRouter(jp, dbKey);
		log.info("数据库路由 method：{} dbIdx：{} tbIdx：{}", WrenchAopUtil.getTargetMethodFromJP(jp)
			.getName(), result.dbIdx, result.tbIdx);
		// 返回结果
		try
		{
			return jp.proceed();
		}
		finally
		{
			DBContextHolder.clearDBKey();
			DBContextHolder.clearTBKey();
		}
	}
}
