package io.github.quiethappiness.wrench.traffic.control.types.annotations.enable;

import org.springframework.context.annotation.ComponentScan;

/**
* 开启白名单，添加此注解。需要
 * 1.开启白名单需要先开启限流 @EnableRateLimiter，否则无效
 * 2.实现接口，提供响应类型的数据源，之后才可以在主街上使用相应类型的白名单。
 * 说明：目前不支持同时开启多个类型的白名单
 * xxx允许同时开启多个类型的白名单，规则是仅满足某一个白名单的请求才会通过xxx
 */
@ComponentScan(value = {"io.github.quiethappiness.wrench.traffic.control.domain.service.whitelist"})
public @interface EnableWhiteList
{
}
