package io.github.quiethappiness.lua.manager.config;

import io.github.quiethappiness.lua.manager.domain.service.manager.ILuaScriptManager;
import io.github.quiethappiness.lua.manager.types.annotations.LuaScriptPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.lang.reflect.Method;

/**
 * LuaBeanPostProcessor
 *
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 动态配置中心服务实现类
 * @date 2025/9/10 16:38
 */
// @Component
@Slf4j
@RequiredArgsConstructor
public class LuaBeanPostProcessor implements BeanPostProcessor
{
	private final ILuaScriptManager luaScriptManager;
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
	{
		
		// 获取当前bean的类对象
		Class<?> targetClass = bean.getClass();
		// 初始化目标对象为传入的bean
		Object targetObject = bean;
		// 检查bean是否是AOP代理对象，如果是则获取其目标类和目标对象
		if (AopUtils.isAopProxy(bean))
		{
			targetClass = AopUtils.getTargetClass(bean);
			targetObject = AopProxyUtils.getSingletonTarget(bean);
		}
		// 获取目标类的所有声明字段
		LuaScriptPath annotation = targetClass.getAnnotation(LuaScriptPath.class);
		// 开始进行扫描lua脚本路径
		if (annotation == null || !annotation.autoRegister())
		{
			Method[] declaredMethods = targetClass.getDeclaredMethods();
			for (Method method : declaredMethods)
			{
				annotation = method.getAnnotation(LuaScriptPath.class);
				if (annotation == null || !annotation.autoRegister())
				{
					continue;
				}
				String fileFullPath = annotation.fileFullPath();
				assert !StringUtils.isBlank(fileFullPath);
				ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
				Resource resource = resolver.getResource(fileFullPath);
				log.info("正在注册Lua脚本...");
				luaScriptManager.initSingleScript(resource, annotation.version());
			}
		}
		else
		{
			// 扫描并注册脚本
			String folderPath = annotation.folderPath();
			assert !StringUtils.isBlank(folderPath);
			log.info("正在注册Lua脚本...");
			luaScriptManager.initScripts(folderPath, annotation.version());
			// 返回原始bean对象
		}
		return bean;
	}
}