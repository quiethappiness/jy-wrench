package io.github.quiethappiness.wrench.traffic.control.types.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UniqueIdentifier
 * @description 唯一标识，用于重复内容的请求
 * @author quietHappiness @jingyue
 * @date 2025/11/3 10:34
 * @version 1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueIdentifier
{
	/**
	 * 参与生成唯一标识的字段名
	 */
	String[] fieldPathsOrExpressions() default {};
	
	/**
	 * 连接符,这里是语法连接符号，可以用于多层字段
	 */
	String Connector() default ".";
	
	Type type() default Type.SPEL;
	
	/**
	 * 哈希算法
	 */
	HashAlgorithm algorithm() default HashAlgorithm.MD5;
	
	enum HashAlgorithm
	{
		MD5,
		SHA256,
		SHA1,
		MURMUR
	}
	
	enum Type
	{
		SPEL,
		CUSTOM,
		;
	}
}