package io.github.quiethappiness.wrench.traffic.control.types.annotations;

import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * WhiteListChecker
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 在需要使用到的白名单服务的接口上，添加此注解并配置必要的信息。接口入参提取字段属性名称、拦截后的返回信息
 * @date 2025/9/28 22:16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface WhiteListChecker
{
	/**
	 * 白名单字段属性名称,这里可以使用SpEL表达式
	 * 使用#{}作为定界符
	 * 支持属性访问：#{user.name}
	 * 支持方法调用：#{user.getName()}
	 * 支持运算符：+、-、&&、||等
	 * 支持集合操作：#{users.?[age > 18]}
	 * 支持字符串操作：#{user.name.toUpperCase()}
	 * 支持正则匹配：#{user.name.matches('[a-zA-Z]+')}
	 */
	@AliasFor("whiteListField")
	String key() default "";
	
	@AliasFor("key")
	String whiteListField() default "";
	
	/**
	 * 白名单类型
	 */
	WhiteListType type() default WhiteListType.USER_ID;
	
	/**
	 * 降级方法，仅在只使用白名单服务时并且失败时触发
	 */
	String fallbackMethod();
}

