package io.github.quiethappiness.wrench.dynamic.config.center.domain.service;

import io.github.quiethappiness.wrench.dynamic.config.center.config.DynamicConfigCenterAutoProperties;
import io.github.quiethappiness.wrench.dynamic.config.center.domain.model.valobj.AttributeVO;
import io.github.quiethappiness.wrench.dynamic.config.center.types.annotations.DCCValue;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DynamicConfigCenterServiceImpl
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 动态配置中心服务实现类
 * @date 2025/9/10 16:38
 */
@Service
public class DynamicConfigCenterServiceImpl implements IDynamicConfigCenterService
{
	private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterServiceImpl.class);
	private final DynamicConfigCenterAutoProperties properties;
	private final RedissonClient redissonClient;
	private Map<String, Object> dccBeanGroup = new ConcurrentHashMap<>();
	
	public DynamicConfigCenterServiceImpl(DynamicConfigCenterAutoProperties dynamicConfigCenterAutoProperties,  RedissonClient redissonClient)
	{
		this.properties = dynamicConfigCenterAutoProperties;
		this.redissonClient = redissonClient;
	}
	
	@Override
	public Object proxyObject(Object bean)
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
		Field[] declaredFields = targetClass.getDeclaredFields();
		for (Field field : declaredFields)
		{
			// 如果字段没有被DCCValue注解标记，则跳过该字段
			if (!field.isAnnotationPresent(DCCValue.class))
			{
				continue;
			}
			// 获取字段上的DCCValue注解实例
			DCCValue dccValue = field.getAnnotation(DCCValue.class);
			// 获取注解中的value值
			String name = field.getName();
			String defaultValue = dccValue.value();
			// 如果value为空或空白字符串，则抛出运行时异常，提示未配置正确的值
			if (StringUtils.isBlank(defaultValue))
			{
				throw new RuntimeException("[" + name + "] @DCCValue is not config value config case 「isSwitch/isSwitch:1」");
			}
			String key = properties.getKey(name);
			String setValue = defaultValue;
			try
			{
				// 再次检查effectiveValue是否为空或空白字符串，如果为空则抛出运行时异常，提示需要配置默认值
				if (StringUtils.isBlank(defaultValue))
				{
					throw new RuntimeException("dcc config error [" + key + "] is not null - 请配置默认值！");
				}
				// 从Redisson客户端获取指定key的RBucket对象
				RBucket<Object> rBucket = redissonClient.getBucket(key);
				// 如果RBucket不存在，则将effectiveValue设置到该key对应的bucket中
				if (!rBucket.isExists())
				{
					rBucket.set(defaultValue);
				}
				else
				{
					// 否则，从RBucket中获取实际的值，并赋给setValue
					setValue = (String) rBucket.get();
				}
				// 设置字段可访问（即使它是私有的）
				field.setAccessible(true);
				// 将获取到的值设置到字段中
				field.set(targetObject, setValue);
				// 恢复字段的访问权限
				field.setAccessible(false);
			}
			catch (IllegalAccessException e)
			{
				// 如果在设置字段值的过程中发生非法访问异常，则抛出运行时异常
				throw new RuntimeException(e);
			}
			// 将key和目标对象放入dccObjGroup中，以便后续管理
			dccBeanGroup.put(key, targetObject);
		}
		// 返回原始bean对象
		return bean;
	}
	
	/**
	 * 调整属性值
	 * @param attributeVO
	 */
	@Override
	public void adjustAttributeValue(AttributeVO attributeVO)
	{
		String name = attributeVO.getName();
		String value = attributeVO.getValue();
		String key = properties.getKey(name);
		RBucket<Object> rBucket = redissonClient.getBucket(key);
		if (!rBucket.isExists())
		{
			return;
		}
		rBucket.set(value);
		Object targetBean = dccBeanGroup.get(key);
		if (targetBean == null)
		{
			return;
		}
		// 获取目标类所有声明字段,取不到就抛异常
		Class<?> targetClass = targetBean.getClass();
		if (AopUtils.isAopProxy(targetBean))
		{
			// 获取代理对象的目标对象
			targetClass = AopUtils.getTargetClass(targetBean);
		}
		// 检查 objBean 是否是代理对象
		// if (!field.isAnnotationPresent(DCCValue.class))
		// {
		// 	return;
		// }
		// 如果字段没有被DCCValue注解标记，则跳过该字段
		try
		{
			Field field = targetClass.getDeclaredField(name);
			// 设置字段可访问（即使它是私有的）
			field.setAccessible(true);
			// 将获取到的值设置到字段中
			field.set(targetBean, value);
			// 恢复字段的访问权限
			field.setAccessible(false);
			log.warn("DCC 节点监听，动态设置值 {} {}", key, value);
		}
		catch (Exception e)
		{
			log.error("DCC 节点监听，动态设置值失败 {}: {}", key, value);
			// 如果在设置字段值的过程中发生非法访问异常，则抛出运行时异常
			throw new RuntimeException(e);
		}
		// 将key和目标对象放入dccObjGroup中，以便后续管理
	}
}