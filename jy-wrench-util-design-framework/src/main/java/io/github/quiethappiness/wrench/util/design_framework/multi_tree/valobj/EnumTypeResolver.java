package io.github.quiethappiness.wrench.util.design_framework.multi_tree.valobj;

import lombok.Data;
import lombok.Getter;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 枚举类型解析器
 */
@Data
public class EnumTypeResolver
{
	private final IRuleEnumRegistry enumRegistry;
	private final Map<String, TypeToken<?>> typeTokenCache = new ConcurrentHashMap<>();
	
	public EnumTypeResolver(IRuleEnumRegistry enumRegistry)
	{
		this.enumRegistry = enumRegistry;
	}
	
	/**
	 * 根据类型名称获取TypeToken
	 */
	@SuppressWarnings("unchecked")
	public <T extends Enum<T> & TypedEnum> TypeToken<T> resolveTypeToken(String typeName)
	{
		return (TypeToken<T>) typeTokenCache.computeIfAbsent(typeName, name ->
		{
			// 通过枚举注册表获取枚举实例，然后获取其Class
			TypedEnum sample = enumRegistry.getAnyEnum(name);
			if (sample != null)
			{
				Class<?> enumClass = sample.getClass();
				return new TypeToken<>((Class<T>) enumClass);
			}
			throw new IllegalArgumentException("未知的枚举类型: " + name);
		});
	}
	
	/**
	 * 类型令牌 - 用于在运行时保留泛型信息
	 */
	@Getter
	public static class TypeToken<T>
	{
		private final Class<T> type;
		
		@SuppressWarnings("unchecked")
		public TypeToken()
		{
			this.type = (Class<T>) ((ParameterizedType)
				getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		}
		
		public TypeToken(Class<T> type)
		{
			this.type = type;
		}
	}
}
