package io.github.quiethappiness.wrench.traffic.control.domain.service.ratelimit.tree.func;

import io.github.quiethappiness.lua.manager.domain.service.manager.ILuaScriptManager;
import io.github.quiethappiness.lua.manager.types.annotations.LuaScriptPath;
import io.github.quiethappiness.wrench.dynamic.config.center.config.DynamicConfigCenterAutoProperties;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class SlidingWindowRateLimiter
{
	public static final String MAO_HAO = ":";
	public static final String RATE_LIMIT = "rateLimit";
	public static final String PREFIX_RATE_LIMIT_SUFFIX = MAO_HAO + RATE_LIMIT + MAO_HAO;
	private final ILuaScriptManager scriptManager;
	
	private final DynamicConfigCenterAutoProperties dynamicConfigCenterAutoProperties;
	
	/**
	 * 尝试获取一个许可
	 * @param key
	 * 	限流键（如按用户ID或IP）
	 * @param windowSizeMs
	 * 	窗口大小（毫秒）
	 * @param maxRequests
	 * 	窗口内最大请求数
	 * @return true 如果请求允许，false 如果被限流
	 */
	@LuaScriptPath(fileFullPath = "script/rateLimit.lua")
	public boolean tryAcquire(String key, long windowSizeMs, long maxRequests)
	{
		String scriptName = RATE_LIMIT;
		String rateLimitKey = spliceRateLimiterKey(key);
		long now = System.currentTimeMillis();
		// 定义Lua脚本
		// 执行脚本（返回值1代表通过，0代表拒绝）
		long result = (long) scriptManager.executeScript(
			scriptName,
			RScript.Mode.READ_WRITE,
			RScript.ReturnType.INTEGER,
			Collections.singletonList(rateLimitKey),
			Long.toString(now),                 // ARGV[1]
			String.valueOf(windowSizeMs),   // ARGV[2]
			String.valueOf(maxRequests)     // ARGV[3]
		);
		return result == 1L;
	}
	
	private String spliceRateLimiterKey(String key)
	{
		return dynamicConfigCenterAutoProperties.getSystem() + PREFIX_RATE_LIMIT_SUFFIX + getClass().getSimpleName() + MAO_HAO + key;
	}
}
