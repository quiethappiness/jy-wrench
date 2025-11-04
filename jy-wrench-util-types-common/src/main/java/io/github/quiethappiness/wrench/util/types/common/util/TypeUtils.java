package io.github.quiethappiness.wrench.util.types.common.util;

/**
 * TypeUtils
 * @description 类型工具类
 * @author quietHappiness @jingyue
 * @date 2025/11/4 10:23
 * @version 1.0
 */
public class TypeUtils
{
	
	/**
	 * 判断是否是八大基本数据类型
	 */
	public static boolean isPrimitive(Class<?> clazz)
	{
		return clazz.isPrimitive();
	}
	
	/**
	 * 判断是否是包装类
	 */
	public static boolean isWrapper(Class<?> clazz)
	{
		return clazz == Integer.class || clazz == Long.class ||
			clazz == Double.class || clazz == Float.class ||
			clazz == Boolean.class || clazz == Byte.class ||
			clazz == Character.class || clazz == Short.class;
	}
	
	/**
	 * 判断是否是基本数据类型或包装类
	 */
	public static boolean isPrimitiveOrWrapper(Class<?> clazz)
	{
		return isPrimitive(clazz) || isWrapper(clazz);
	}
	
	/**
	 * 判断是否是目标类型（八大基本数据类型、包装类、String）
	 */
	public static boolean isTargetType(Class<?> clazz)
	{
		return isPrimitiveOrWrapper(clazz) || clazz == String.class;
	}
	
	/**
	 * 获取对应的包装类
	 */
	public static Class<?> getWrapperClass(Class<?> clazz)
	{
		if (!clazz.isPrimitive())
		{
			return clazz;
		}
		if (clazz == int.class)
		{
			return Integer.class;
		}
		if (clazz == long.class)
		{
			return Long.class;
		}
		if (clazz == double.class)
		{
			return Double.class;
		}
		if (clazz == float.class)
		{
			return Float.class;
		}
		if (clazz == boolean.class)
		{
			return Boolean.class;
		}
		if (clazz == byte.class)
		{
			return Byte.class;
		}
		if (clazz == char.class)
		{
			return Character.class;
		}
		if (clazz == short.class)
		{
			return Short.class;
		}
		return clazz;
	}
}