
package io.github.quiethappiness.wrench.traffic.control.types.annotations.enable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启白名单，添加此注解。需要
 * 1.开启白名单需要先开启限流 @EnableRateLimiter，否则无效
 * 2.实现接口，提供响应类型的数据源，之后才可以在主街上使用相应类型的白名单。
 * 说明：目前不支持同时开启多个类型的白名单
 * xxx允许同时开启多个类型的白名单，规则是仅满足某一个白名单的请求才会通过xxx
 */
// @Import(WhiteListImportSelector.class)
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时保留，这是必须的[2,4](@ref)
@Target(ElementType.TYPE) // 注解用于类、接口或枚举声明[4](@ref)
public @interface EnableWhiteList
{
}

