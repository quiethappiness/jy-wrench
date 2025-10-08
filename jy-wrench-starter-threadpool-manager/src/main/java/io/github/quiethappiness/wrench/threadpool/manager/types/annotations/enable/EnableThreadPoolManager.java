package io.github.quiethappiness.wrench.threadpool.manager.types.annotations.enable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 注解在运行时保留，这是必须的[2,4](@ref)
@Target(ElementType.TYPE) // 注解用于类、接口或枚举声明[4](@ref)
// @Import(MethodExtensionConfiguration.class)
public @interface EnableThreadPoolManager
{
}
