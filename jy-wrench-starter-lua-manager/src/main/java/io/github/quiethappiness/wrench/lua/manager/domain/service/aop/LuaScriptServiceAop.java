package io.github.quiethappiness.wrench.lua.manager.domain.service.aop;

import io.github.quiethappiness.wrench.lua.manager.types.annotations.LuaScriptPath;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

import static io.github.quiethappiness.wrench.lua.manager.domain.service.manager.AbstractLuaScriptManager.LOCAL_SEPARATOR;

/**
 * LuaScriptServiceAop
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description Lua脚本服务AOP处理器
 * @date 2025/9/9
 */
@Aspect
@Slf4j
public class LuaScriptServiceAop
{
	/**
	 * 拦截带有 @LuaScriptPath 注解的类的所有方法
	 */
	@Pointcut("@within(io.github.quiethappiness.wrench.lua.manager.types.annotations.LuaScriptPath)")
	public void luaScriptClassPointcut()
	{
	}
	/**
	 * 定义切点：拦截带有 @LuaScriptMethod 注解的方法
	 */
	// @Pointcut("@annotation(io.github.quiethappiness.lua.manager.types.annotations.LuaScriptPath)")
	// default void luaScriptMethodPointcut(LuaScriptPath luaScriptMethod) {}
	
	/**
	 * 环绕通知：处理 Lua 脚本方法调用
	 */
	@Around("luaScriptClassPointcut()")
	public Object aroundLuaScriptService(ProceedingJoinPoint joinPoint) throws Throwable
	{
		// 获取目标类和方法信息
		Class<?> targetClass = joinPoint.getTarget()
			.getClass();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		String methodName = method.getName();
		// 获取类上的注解
		LuaScriptPath classAnnotation = targetClass.getAnnotation(LuaScriptPath.class);
		if (classAnnotation != null && classAnnotation.autoRegister())
		{
			// 构建脚本名称
			String serviceName = getDefaultServiceName(targetClass);
			String scriptName = serviceName + LOCAL_SEPARATOR + methodName;
			log.debug("准备执行 Lua 脚本: {}", scriptName);
			long startTime = System.currentTimeMillis();
			try
			{
				// 执行原方法
				Object result = joinPoint.proceed();
				long executionTime = System.currentTimeMillis() - startTime;
				log.debug("Lua 脚本 {} 执行成功，耗时: {}ms", scriptName, executionTime);
				return result;
			}
			catch (Exception e)
			{
				long executionTime = System.currentTimeMillis() - startTime;
				log.error("Lua 脚本 {} 执行失败，耗时: {}ms，错误: {}",
					scriptName, executionTime, e.getMessage(), e);
				throw e;
			}
		}
		else
		{
			// 如果没有注解，直接执行原方法
			return joinPoint.proceed();
		}
	}
	
	/**
	 * 根据类名获取默认服务名称
	 */
	private String getDefaultServiceName(Class<?> clazz)
	{
		String className = clazz.getSimpleName();
		if (className.endsWith("Service"))
		{
			return className.substring(0, className.length() - 7)
				.toLowerCase();
		}
		return className.toLowerCase();
	}
}
