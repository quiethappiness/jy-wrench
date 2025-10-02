package io.github.quiethappiness.wrench.traffic.control.types.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface TcHystrix
{
	int timeout() default 1000;
	
	int retries() default 3;
	
	String returnJson() default "{}";
	
	String fallbackMethod() default "";
}
