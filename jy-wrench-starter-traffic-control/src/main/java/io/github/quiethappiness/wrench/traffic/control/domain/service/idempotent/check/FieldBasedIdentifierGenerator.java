package io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.check;

import io.github.quiethappiness.wrench.traffic.control.types.annotations.UniqueIdentifier;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.getCompositionFieldValueByFieldPath;
import static io.github.quiethappiness.wrench.aop.util.WrenchAopUtil.getTargetMethodFromJP;

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
	
	private final ExpressionParser parser = new SpelExpressionParser();
	private final StandardEvaluationContext context = new StandardEvaluationContext();
	/**
	 * 根据注解配置生成唯一标识
	 */
	public String generateIdentifier(ProceedingJoinPoint pjp) throws NoSuchMethodException
	{
		Method method = getTargetMethodFromJP(pjp);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		List<String> identifiers = new ArrayList<>();
		for (int i = 0; i < parameterAnnotations.length; i++)
		{
			for (Annotation annotation : parameterAnnotations[i])
			{
				if (annotation instanceof UniqueIdentifier uniqueIdentifier)
				{
					identifiers.add(generateIdentifierFromFields(pjp.getArgs()[i], uniqueIdentifier));
				}
			}
		}
		
		if(CollectionUtils.isEmpty(identifiers))
		{
			identifiers.add(generateIdentifierFromAllFields(pjp.getArgs()[0]));
		}
		// 默认以逗号分隔
		return identifiers.stream().reduce("", (a, b) -> a +";"+ b);
	}
	
	/**
	 * 根据指定字段生成标识
	 */
	public String generateIdentifierFromFields(Object obj, UniqueIdentifier uniqueIdentifier )
	{
		String[] fields = uniqueIdentifier.fieldPaths();
		String connector = uniqueIdentifier.Connector();
		String separator = uniqueIdentifier.separator();
		UniqueIdentifier.HashAlgorithm algorithm = uniqueIdentifier.algorithm();
		try
		{
			List<String> fieldValues = new ArrayList<>();
			
			for (String fieldName : fields)
			{
				Object value = getCompositionFieldValueByFieldPath(obj, fieldName, connector);
				fieldValues.add(value != null ? value.toString() : "null");
			}
			
			String concatenated = String.join(separator, fieldValues);
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
			String concatenated = String.join("|", fieldValues);
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