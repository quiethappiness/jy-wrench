package io.github.quiethappiness.wrench.traffic.control.types.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 自定义注解：用于标记参数作为唯一标识
 * 该注解可以应用于方法的参数上，用于指定生成唯一标识所需的字段和配置
 */
@Target(ElementType.PARAMETER)  // 指定该注解只能用于参数上
@Retention(RetentionPolicy.RUNTIME)  // 指定该注解的保留策略为运行时
public @interface UniqueIdentifier
{
	/**
	 * 参与生成唯一标识的字段名.例如
	 * <p>spel表达式</p>
	 * <pre>
	 *     #{user.id}
	 * </pre>
	 * <pre>
	 *     fieldPathsOrExpressions = {"#user.code", "#user.info"}
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