package io.github.quiethappiness.lua.manager.types.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface LuaScriptPath
{
	/**
	 * 脚本文件夹路径
	 */
	String folderPath() default "";

	String fileFullPath() default "";
	/**
	 * 是否启用自动注册
	 */
	boolean autoRegister() default true;
	
	/**
	 * 脚本版本号
	 */
	String version() default "1.0";
}
