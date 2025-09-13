package io.github.quiethappiness.wrench.rate.limiter.domain.service.tree;

import io.github.quiethappiness.wrench.design.framework.tree.AbstractMultiThreadStrategyRouter;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.RequestParameterEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.model.entity.ResponseResultEntity;
import io.github.quiethappiness.wrench.rate.limiter.domain.service.tree.factory.RateLimiterStrategyFactory;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;

public abstract class AbstractRateLimiterSupport extends AbstractMultiThreadStrategyRouter<RequestParameterEntity, RateLimiterStrategyFactory.DynamicContext, ResponseResultEntity>
{
	
	@Override
	protected void multiThread(RequestParameterEntity requestParameter, RateLimiterStrategyFactory.DynamicContext dynamicContext) throws Exception
	{
		// 缺省的方法
	}
	
	/**
	 * 实际根据自身业务调整，主要是为了获取通过某个值做拦截
	 */
	protected String getAttrValue(String attrName, Object[] args)
	{
		if (args[0] instanceof String)
		{
			return args[0].toString();
		}
		String filedValue = null;
		for (Object arg : args)
		{
			try
			{
				if (StringUtils.isNotBlank(filedValue))
				{
					break;
				}
				// filedValue = BeanUtils.getProperty(arg, attrName);
				// fix: 使用lombok时，uId这种字段的get方法与idea生成的get方法不同，会导致获取不到属性值，改成反射获取解决
				filedValue = String.valueOf(this.getValueByFieldName(arg, attrName));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return filedValue;
	}
	
	/**
	 * 获取对象的特定属性值
	 * @param bean
	 * 	对象
	 * @param name
	 * 	属性名
	 * @return 属性值
	 * @author tang
	 */
	public Object getValueByFieldName(Object bean, String name)
	{
		try
		{
			Field field = getFieldByName(bean, name);
			if (field == null)
			{
				return null;
			}
			field.setAccessible(true);
			Object o = field.get(bean);
			field.setAccessible(false);
			return o;
		}
		catch (IllegalAccessException e)
		{
			return null;
		}
	}
	
	/**
	 * 根据名称获取方法，该方法同时兼顾继承类获取父类的属性
	 * @param item
	 * 	对象
	 * @param name
	 * 	属性名
	 * @return 该属性对应方法
	 * @author tang
	 */
	public Field getFieldByName(Object item, String name)
	{
		try
		{
			Field field;
			try
			{
				field = item.getClass()
					.getDeclaredField(name);
			}
			catch (NoSuchFieldException e)
			{
				field = item.getClass()
					.getSuperclass()
					.getDeclaredField(name);
			}
			return field;
		}
		catch (NoSuchFieldException e)
		{
			return null;
		}
	}
}