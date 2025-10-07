package io.github.quiethappiness.wrench.lua.manager.domain.service.aop;

import io.github.quiethappiness.wrench.lua.manager.config.LuaManagerProperties;
import io.github.quiethappiness.wrench.lua.manager.domain.model.valobj.ScriptNameContext;
import io.github.quiethappiness.wrench.lua.manager.types.annotations.LuaScriptPath;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
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
	
	/**
	 * 定义切点：拦截带有 @LuaScriptMethod 注解的方法
	 */
	// @Pointcut("@annotation(io.github.quiethappiness.lua.manager.types.annotations.LuaScriptPath)")
	// default void luaScriptMethodPointcut(LuaScriptPath luaScriptMethod) {}
	{
		log.info("LuaScriptServiceAop 正在初始化...");
	}
	
	/**
	 * 环绕通知：处理 Lua 脚本方法调用
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
		// 获取类上的注解
		LuaScriptPath classAnnotation = targetClass.getAnnotation(LuaScriptPath.class);
		LuaScriptPath methodAnnotation = method.getAnnotation(LuaScriptPath.class);
		if (methodAnnotation == null && classAnnotation == null)
		{
			// 如果没有注解，直接执行原方法
			return joinPoint.proceed();
		}
		String fileFullPath = "";
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
		if (!StringUtils.hasText(fileFullPath))
		{
			throw new IllegalArgumentException("脚本lua文件路径不能为空");
		}
		String scriptName = fileFullPath.substring(0, fileFullPath.lastIndexOf(".lua"));
		ScriptNameContext.setScriptName(scriptName);
		// Object[] args = joinPoint.getArgs();
		// if (args == null || args.length == 0)
		// {
		// 	throw new IllegalArgumentException("参数不能为空");
		// }
		//
		// ILuaScriptManager.LuaScriptExecuteVO luaScriptExecuteVO = (ILuaScriptManager.LuaScriptExecuteVO) args[0];
		// luaScriptExecuteVO.setScriptName(scriptName);
		// log.info("Lua 脚本正在填充参数: {}", luaScriptExecuteVO);
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