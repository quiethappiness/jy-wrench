package io.github.quiethappiness.db.router.domain.service;

import org.aspectj.lang.annotation.Pointcut;

public interface IDBRouterAOP
{
	
	@Pointcut("@annotation(io.github.quiethappiness.db.router.types.annotations.DBRouter)")
	default void aopPoint()
	{
	}
	
}
