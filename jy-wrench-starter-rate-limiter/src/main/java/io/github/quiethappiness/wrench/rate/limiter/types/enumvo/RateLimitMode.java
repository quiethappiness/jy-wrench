package io.github.quiethappiness.wrench.rate.limiter.types.enumvo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RateLimitMode
{
	PPS("pps", "Requests per second"),
	PPS_BLACKLIST("pps_blacklist", "Requests per second with blacklist"),
	SWR("swr", "Slide window rate limiter"),
	SWR_BLACKLIST("swr_blacklist", "Slide window rate limiter with blacklist"),
	;
	String name;
	String desc;
}
