package io.github.quiethappiness.wrench.traffic.control.types.enumvo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TrafficMode
{
	/**
	 * 白名单模式，白名单内的用户不受限，其他用户受限
	 */
	// WHITELIST("whitelist", "Whitelist mode"),
	/**
	 * 频率模式，用户请求的频率超过限制则被限制
	 */
	PPS("pps", "Requests per second"),
	SWR("swr", "Slide window rate limiter"),
	/**
	 * 混合模式：
	 */
	// PPS+
	PPS_BLACKLIST("pps_blacklist", "Requests per second with blacklist"),
	SWR_BLACKLIST("swr_blacklist", "Slide window rate limiter with blacklist"),
	// WHITELIST_PPS("whitelist_pps", "whitelist mode with Requests per second"),
	// WHITELIST_PPS_BLACKLIST("whitelist_pps_blacklist", "whitelist mode with Requests per second with blacklist"),
	// WHITELIST_SWR("whitelist_swr", "whitelist mode with Slide window rate limiter"),
	// WHITELIST_SWR_BLACKLIST("whitelist_swr_blacklist", "whitelist mode with Slide window rate limiter with blacklist"),
	;
	private final String name;
	private final String desc;
}
