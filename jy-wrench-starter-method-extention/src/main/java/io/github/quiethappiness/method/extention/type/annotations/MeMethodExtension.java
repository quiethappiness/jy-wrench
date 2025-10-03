package io.github.quiethappiness.method.extention.type.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface MeMethodExtension
{
	/**
	 * 前置方法
	 * @return
	 */
	String beforeMethod() default "";
	
	String beforeReturnJson() default "{}";
	
	String afterReturnMethod() default "";
	
	/**
	 * 异常方法
	 * @return
	 */
	String afterThrowingMethod() default "";
	
	/**
	 * 后置方法
	 * @return
	 */
	String afterMethod() default "";
}
