package io.github.quiethappiness.wrench.util.design_framework.multi_tree.valobj;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IRuleEnumRegistry
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description
 * @date 2025/10/23 15:37
 */
public interface IRuleEnumRegistry
{
	Map<String, Map<String, TypedEnum>> registry = new ConcurrentHashMap<>();
	
	default <T extends Enum<T> & TypedEnum> void registerEnum(T[] enumValues)
	{
		for (T enumValue : enumValues)
		{
			String type = enumValue.getType();
			String value = enumValue.getValue();
			registry
				.computeIfAbsent(type, k -> new ConcurrentHashMap<>())
				.put(value, enumValue);
		}
	}
	
	/**
	 * 根据类型和值获取枚举
	 */
	@SuppressWarnings("unchecked")
	default <T extends Enum<T> & TypedEnum> T getEnum(String type, String value)
	{
		Map<String, TypedEnum> typeMap = registry.get(type);
		if (typeMap == null)
		{
			throw new IllegalArgumentException("未知的枚举类型: " + type);
		}
		TypedEnum result = typeMap.get(value);
		if (result == null)
		{
			throw new IllegalArgumentException(String.format("类型 %s 中没有找到值 %s", type, value));
		}
		return (T) result;
	}
	
	/**
	 * 安全获取枚举，找不到时返回null
	 */
	@SuppressWarnings("unchecked")
	default <T extends Enum<T> & TypedEnum> T getEnumSafe(String type, String value)
	{
		Map<String, TypedEnum> typeMap = registry.get(type);
		if (typeMap == null)
		{
			return null;
		}
		return (T) typeMap.get(value);
	}
	
	/**
	 * 获取所有支持的枚举类型
	 */
	default Set<String> getSupportedTypes()
	{
		return Collections.unmodifiableSet(registry.keySet());
	}
	
	/**
	 * TypedEnum
	 * @author quietHappiness @jingyue
	 * @version 1.0
	 * @description
	 * @date 2025/10/23 11:06
	 */
	interface TypedEnum
	{
		
		default String getType()
		{
			return this.getClass()
				.getSimpleName();
		}
		
		String getValue();
		
		default boolean equals(TypedEnum other)
		{
			return this.getValue()
				.equals(other.getValue());
		}
	}
}
