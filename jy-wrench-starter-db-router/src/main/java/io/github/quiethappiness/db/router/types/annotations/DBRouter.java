package io.github.quiethappiness.db.router.types.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DBRouter {
	
	/**
	 * 分库分表的路由key
	 */
	String field() default "";
	
}
