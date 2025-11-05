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
	 * 参与生成唯一标识的字段名.例如
	 * <p>spel表达式</p>
	 * <pre>
	 *     #{user.id}
	 * </pre>
	 * <pre>
	 *     fieldPathsOrExpressions = {"#code", "#info"}
	 * </pre>
	 * 不配置默认走所有字段
	 */
	String[] fieldPathsOrExpressions() ;
	
	/**
	 * 定义一个默认值为Type.SPEL的type()方法
	 * 这是一个注解中的默认值设置，当使用该注解但没有显式指定type值时，将自动使用Type.SPEL作为默认值
	 */
	Type type() default Type.SPEL;
	
	/**
	 * 连接符,这里是语法连接符号，可以用于多层字段。当选择自定义时需要考虑配置
	 */
	String Connector() default ".";
	
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