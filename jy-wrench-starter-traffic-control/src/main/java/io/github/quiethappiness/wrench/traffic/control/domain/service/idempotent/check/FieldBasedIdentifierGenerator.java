package io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.check;

import io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.MethodPart;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.UniqueIdentifier;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.UniqueIdentifier.Type;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.FieldPart;

/**
 * FieldBasedIdentifierGenerator
 * @description
 * @author quietHappiness @jingyue
 * @date 2025/11/3 10:30
 * @version 1.0
 */
@Component
public class FieldBasedIdentifierGenerator
{
	private static final String DEFAULT_CONNECTOR = ".";
	private static final String DEFAULT_SEPARATOR = "|";
	
	/**
	 * 根据注解配置生成唯一标识
	 * 该方法通过获取目标方法的参数注解，检查是否有@UniqueIdentifier注解，
	 * 如果有，则根据注解配置生成标识；如果没有，则使用所有字段生成标识
	 * <p>看似是扫描所有参数，其实多数情况下只有一个参数，毕竟@RequestBody</p>
	 *
	 * @param pjp 连接点对象，包含方法调用的相关信息
	 * @return 生成的唯一标识字符串
	 * @throws NoSuchMethodException 当无法找到目标方法时抛出
	 */
	public String generateIdentifier(ProceedingJoinPoint pjp) throws NoSuchMethodException
	{
		// 获取目标方法对象
		Method method = MethodPart.getTargetMethodFromJP(pjp);
		// 获取方法参数的注解数组
		Parameter[] parameters = method.getParameters();
		Object[] args = pjp.getArgs();
		// 用于存储生成的标识列表
		List<String> identifiers = new ArrayList<>();
		// 遍历所有参数的注解
		for (int i = 0; i < parameters.length; i++)
		{
			// 遍历当前参数的所有注解
			UniqueIdentifier[] uniqueIdentifiers = parameters[i].getAnnotationsByType(UniqueIdentifier.class);
			if (uniqueIdentifiers.length > 0)
			{
				// 如果有@UniqueIdentifier注解，则根据注解配置生成标识并添加到列表
				identifiers.add(generateIdentifierFromFields(parameters[i].getType(), parameters[i].getName(), args[i], uniqueIdentifiers[0]));
			}
		}
		// 如果没有找到任何@UniqueIdentifier注解，则使用第一个参数的所有字段生成标识
		if (CollectionUtils.isEmpty(identifiers))
		{
			identifiers.add(generateIdentifierFromAllFields(args[0]));
		}
		// 默认以逗号分隔
		return identifiers
			.stream()
			.reduce("", (a, b) -> a + ";" + b);
	}
	
	/**
	 * 根据指定字段生成标识
	 */
	public String generateIdentifierFromFields(Class<?> clazz, String parameterName, Object parameterValue, UniqueIdentifier uniqueIdentifier)
	{
		String[] fieldPathsOrExpressions = uniqueIdentifier.fieldPathsOrExpressions();
		String connector = uniqueIdentifier.Connector();
		Type type = uniqueIdentifier.type();
		UniqueIdentifier.HashAlgorithm algorithm = uniqueIdentifier.algorithm();
		try
		{
			List<String> fieldValues = new ArrayList<>();
			if (type == Type.SPEL)
			{
				for (String expression : fieldPathsOrExpressions)
				{
					EvaluationContext context = FieldPart.buildEvaluationContext(parameterName, parameterValue);
					Object expressionValue = FieldPart.extractSpelExpressionValue(context, expression, clazz);
					fieldValues.add(expressionValue != null ? expressionValue.toString() : "null");
				}
			}
			else if (type == Type.CUSTOM)
			{
				for (String fieldPath : fieldPathsOrExpressions)
				{
					Object value = FieldPart.getCompositionFieldObjectByFieldPath(parameterName, parameterValue, fieldPath, connector);
					fieldValues.add(value != null ? value.toString() : "null");
				}
			}
			String concatenated = String.join(DEFAULT_SEPARATOR, fieldValues);
			return hashString(concatenated, algorithm);
		}
		catch (Exception e)
		{
			throw new RuntimeException("生成唯一标识失败: " + e.getMessage(), e);
		}
	}
	
	/**
	 * 使用所有字段生成标识
	 */
	public String generateIdentifierFromAllFields(Object obj)
	{
		try
		{
			List<String> fieldValues = new ArrayList<>();
			Field[] fields = obj
				.getClass()
				.getDeclaredFields();
			for (Field field : fields)
			{
				field.setAccessible(true);
				Object value = field.get(obj);
				fieldValues.add(value != null ? value.toString() : "null");
			}
			// 按字段名排序确保一致性
			Collections.sort(fieldValues);
			String concatenated = String.join(DEFAULT_SEPARATOR, fieldValues);
			return hashString(concatenated, UniqueIdentifier.HashAlgorithm.SHA256);
		}
		catch (Exception e)
		{
			throw new RuntimeException("生成唯一标识失败: " + e.getMessage(), e);
		}
	}
	
	/**
	 * 字符串哈希
	 */
	private String hashString(String input, UniqueIdentifier.HashAlgorithm algorithm)
	{
		try
		{
			MessageDigest digest;
			switch (algorithm)
			{
				case MD5:
					digest = MessageDigest.getInstance("MD5");
					break;
				case SHA1:
					digest = MessageDigest.getInstance("SHA-1");
					break;
				case SHA256:
					digest = MessageDigest.getInstance("SHA-256");
					break;
				case MURMUR:
					return String.valueOf(murmurHash(input));
				default:
					digest = MessageDigest.getInstance("SHA-256");
			}
			byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			return bytesToHex(hash);
		}
		catch (Exception e)
		{
			throw new RuntimeException("哈希计算失败", e);
		}
	}
	
	private long murmurHash(String input)
	{
		// 简化实现，实际应该使用完整的MurmurHash实现
		return Math.abs(input.hashCode());
	}
	
	private String bytesToHex(byte[] bytes)
	{
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes)
		{
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1)
			{
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}
}