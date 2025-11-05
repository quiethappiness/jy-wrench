package io.github.quiethappiness.wrench.traffic.control.types.annotations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;


/**
 * <p>在需要使用到的白名单服务的接口上，添加此注解并配置必要的信息。接口入参提取字段属性名称、拦截后的返回信息</p>
 * 自定义注解 TcWhiteList，用于标记白名单相关的方法
 * 该注解具有运行时保留、可继承、可文档化等特性，并只能用于方法上
 */
@Retention(RetentionPolicy.RUNTIME)  // 注解保留在运行时
@Target(ElementType.METHOD)         // 注解只能用于方法上
@Inherited                          // 注解可被继承
@Documented                         // 注解会包含在JavaDoc中
public @interface TcWhiteList
{
	/**
	 * 白名单字段属性名称,这里可以使用SpEL表达式
	 * 使用#{}作为定界符
	 * <p>支持属性访问：<pre>
	 *     #{user.name}
	 * </pre></p>
	 * <p>支持方法调用：<pre>
	 *     #{user.getName()}
	 * </pre></p>
	 * <p>支持运算符：<pre>
	 *     +、-、&&、||等
	 * </pre></p>
	 * <p>支持集合操作：<pre>
	 *     #{users.?[age > 18]}
	 * </pre></p>
	 * <p>支持字符串操作：<pre>
	 *     #{user.name.toUpperCase()}
	 * </pre></p>
	 * <p>支持正则匹配：<pre>
	 *     #{user.name.matches('[a-zA-Z]+')}
	 * </pre></p>
	 */
	@AliasFor("whiteListField")
	String key() default "";         // 指定白名单的key，默认为空
	
	@AliasFor("key")
	String whiteListField() default "";  // 指定白名单的字段，默认为空
	
	/**
	 * 白名单类型
	 */
	WhiteListType type() default WhiteListType.USER_ID;  // 指定白名单类型，默认为USER_ID
	
	/**
	 * 降级方法，仅在只使用白名单服务时并且失败时触发
	 */
	String fallbackMethod();
	
	@Getter
	@AllArgsConstructor
	enum WhiteListType
	{
		USER_ID("actualValue", "UserId", String.class,"userIdWhiteListDataProvider"),
		IP("ip", "IP", String.class,"ipWhiteListDataProvider"),
		PHONE("phone", "Phone", String.class,"phoneWhiteListDataProvider"),
		// EMAIL("email", "Email"),
		// HEADER("header", "Header"),
		// COOKIE("cookie", "Cookie"),
		// ALL("all", "All"),
		;
		private final String code;
		private final String desc;
		private final Class<?> clazz;
		private final String dataProviderName;
	}
}