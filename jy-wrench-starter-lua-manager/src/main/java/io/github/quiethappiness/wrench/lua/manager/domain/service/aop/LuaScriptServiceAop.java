package io.github.quiethappiness.wrench.lua.manager.domain.service.aop;

import io.github.quiethappiness.wrench.lua.manager.config.LuaManagerProperties;
import io.github.quiethappiness.wrench.lua.manager.domain.model.valobj.ScriptNameContext;
import io.github.quiethappiness.wrench.lua.manager.types.annotations.LuaScriptPath;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;

import static io.github.quiethappiness.wrench.lua.manager.domain.service.manager.AbstractLuaScriptManager.spliceLuaFilePath;

/**
 * LuaScriptServiceAop
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description Lua脚本服务AOP处理器
 * @date 2025/9/9
 */
@Aspect
@Component
@Slf4j
public class LuaScriptServiceAop
{
	@Resource
	private LuaManagerProperties luaManagerProperties;
	
	/**
	 * 拦截带有 @LuaScriptPath 注解的类的所有方法
	 */
	@Pointcut("@within(io.github.quiethappiness.wrench.lua.manager.types.annotations.LuaScriptPath)")
	public void luaScriptClassPointcut()
	{
	}
	
	@Pointcut("@annotation(io.github.quiethappiness.wrench.lua.manager.types.annotations.LuaScriptPath)")
	public void luaScriptMethodPointcut()
	{
	}
	
	/*
	  定义切点：拦截带有 @LuaScriptMethod 注解的方法
	 */
	// @Pointcut("@annotation(io.github.quiethappiness.lua.manager.types.annotations.LuaScriptPath)")
	// default void luaScriptMethodPointcut(LuaScriptPath luaScriptMethod) {}
	{
		log.info("LuaScriptServiceAop 正在初始化...");
	}
	
	/**
	 * 环绕通知：处理 Lua 脚本方法调用
	 * <p>
	 * 该方法使用AOP环绕通知来拦截带有@LuaScriptPath注解的类或方法，
	 * 实现对Lua脚本执行的监控和上下文管理功能
	 * </p>
	 * 
	 * @param joinPoint 连接点对象，包含目标方法的信息
	 * @return 原方法执行的结果
	 * @throws Throwable 原方法可能抛出的异常
	 */
	@Around("luaScriptClassPointcut()||luaScriptMethodPointcut()")
	public Object aroundLuaScriptService(ProceedingJoinPoint joinPoint) throws Throwable
	{
		// 获取目标类和方法信息
		Class<?> targetClass = joinPoint.getTarget()
			.getClass();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		String methodName = method.getName();
		log.info("正在处理 Lua 脚本方法: {}", methodName);
		
		// 获取类和方法上的注解
		LuaScriptPath classAnnotation = targetClass.getAnnotation(LuaScriptPath.class);
		LuaScriptPath methodAnnotation = method.getAnnotation(LuaScriptPath.class);
		if (methodAnnotation == null && classAnnotation == null)
		{
			// 如果没有注解，直接执行原方法
			return joinPoint.proceed();
		}
		
		// 根据注解配置确定Lua脚本文件路径
		String fileFullPath = "";
		// 如果有注解，优先使用方法上的注解
		if (methodAnnotation != null && methodAnnotation.autoRegister())
		{
			fileFullPath = methodAnnotation.fileFullPath();
		}
		else if (classAnnotation != null && classAnnotation.autoRegister())
		{
			// 构建脚本名称
			// String serviceName = getDefaultServiceName(targetClass);
			fileFullPath = spliceLuaFilePath(luaManagerProperties.getPath(), classAnnotation.folderPath(), methodName);
		}
		
		// 验证脚本文件路径
		if (!StringUtils.hasText(fileFullPath))
		{
			throw new IllegalArgumentException("脚本lua文件路径不能为空");
		}
		
		// 设置脚本名称上下文
		String scriptName = fileFullPath.substring(0, fileFullPath.lastIndexOf(".lua"));
		ScriptNameContext.setScriptName(scriptName);

		log.info("准备执行 Lua 脚本: {}", scriptName);
		Instant startTime = Instant.now();
		Instant endTime = null;
		try
		{
			// 执行原方法
			Object result = joinPoint.proceed();
			endTime = Instant.now();
			log.info("Lua 脚本 {} 执行成功，耗时: {}ms", scriptName, Duration.between(startTime, endTime)
				.toMillis());
			return result;
		}
		catch (Exception e)
		{
			endTime = Instant.now();
			long executionTime = Duration.between(startTime, endTime)
				.toMillis();
			log.error("Lua 脚本 {} 执行失败，耗时: {}ms，错误: {}",
				scriptName, executionTime, e.getMessage(), e);
			throw e;
		}
		finally
		{
			// 清理脚本名称上下文
			ScriptNameContext.clear();
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