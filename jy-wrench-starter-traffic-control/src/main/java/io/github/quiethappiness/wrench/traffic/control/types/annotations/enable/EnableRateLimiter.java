package io.github.quiethappiness.wrench.traffic.control.types.annotations.enable;

import org.springframework.context.annotation.ComponentScan;

@ComponentScan(value = {"io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit"})
public @interface EnableRateLimiter
{
}
