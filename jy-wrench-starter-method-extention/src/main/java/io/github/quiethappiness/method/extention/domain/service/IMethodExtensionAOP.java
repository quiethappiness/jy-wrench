package io.github.quiethappiness.method.extention.domain.service;

import org.aspectj.lang.annotation.Pointcut;

public interface IMethodExtensionAOP
{
	@Pointcut(value = "@annotation(io.github.quiethappiness.method.extention.type.annotations.MeMethodExtension)")
	public default void methodExtensionPointcut(){}
	
}
