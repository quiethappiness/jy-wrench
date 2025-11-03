package io.github.quiethappiness.wrench.util.types.common.util;

/**
 * StringCaseUtils
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 字符串工具
 * @date 2025/10/16 21:11
 */

/**
 * 字符串处理工具类
 */
public final class StringCaseUtils
{
	
	private StringCaseUtils()
	{
		// 工具类，防止实例化
		throw new AssertionError("No " + getClass().getName() + " instances for you!");
	}
	
	/**
	 * 首字母变小写
	 */
	public static String firstLetterToLowerCase(String str)
	{
		if (str == null || str.isEmpty())
		{
			return str;
		}
		
		char firstChar = str.charAt(0);
		if (Character.isLowerCase(firstChar))
		{
			return str;
		}
		
		return Character.toLowerCase(firstChar) + str.substring(1);
	}
	
	/**
	 * 首字母变大写
	 */
	public static String firstLetterToUpperCase(String str)
	{
		if (str == null || str.isEmpty())
		{
			return str;
		}
		
		char firstChar = str.charAt(0);
		if (Character.isUpperCase(firstChar))
		{
			return str;
		}
		
		return Character.toUpperCase(firstChar) + str.substring(1);
	}
	
	/**
	 * 下划线转驼峰命名
	 */
	public static String underscoreToCamelCase(String str)
	{
		if (str == null || str.isEmpty())
		{
			return str;
		}
		
		String[] parts = str.split("_");
		StringBuilder result = new StringBuilder(parts[0]);
		
		for (int i = 1; i < parts.length; i++)
		{
			if (!parts[i].isEmpty())
			{
				result.append(Character.toUpperCase(parts[i].charAt(0)))
					.append(parts[i].substring(1).toLowerCase());
			}
		}
		
		return result.toString();
	}
	
	/**
	 * 驼峰命名转下划线
	 */
	public static String camelCaseToUnderscore(String str)
	{
		if (str == null || str.isEmpty())
		{
			return str;
		}
		
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < str.length(); i++)
		{
			char c = str.charAt(i);
			if (Character.isUpperCase(c) && i > 0)
			{
				result.append('_');
			}
			result.append(Character.toLowerCase(c));
		}
		
		return result.toString();
	}
}