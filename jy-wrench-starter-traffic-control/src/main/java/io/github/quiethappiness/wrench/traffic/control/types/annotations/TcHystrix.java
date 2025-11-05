package io.github.quiethappiness.wrench.traffic.control.types.annotations;

import java.lang.annotation.*;


/**
 * 自定义注解 TcHystrix，用于标记需要使用断路器模式的方法
 * 该注解可以配置超时时间、重试次数、默认返回JSON和降级方法
 *
 * @Retention(RetentionPolicy.RUNTIME) 表示注解会在运行时保留，可以通过反射获取
 * @Target({ElementType.METHOD}) 表示该注解只能用于方法上
 * @Documented 表示注解会被包含在JavaDoc中
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface TcHystrix
{
    /**
     * 设置方法执行的超时时间，单位为毫秒
     * 默认值为1000毫秒（1秒）
     *
     * @return 超时时间
     */
	int timeout() default 1000;
	
    /**
     * 设置方法失败时的重试次数
     * 默认值为3次
     *
     * @return 重试次数
     */
	int retries() default 3;
	
    /**
     * 设置方法执行失败时返回的JSON字符串
     * 默认值为空对象 "{}"
     *
     * @return 返回的JSON字符串
     */
	String returnJson() default "{}";
	
    /**
     * 设置降级方法的名称
     * 当方法执行失败或超时时，将调用此指定的降级方法
     * 默认值为空字符串，表示不使用降级方法
     *
     * @return 降级方法名
     */
	String fallbackMethod() default "";
}