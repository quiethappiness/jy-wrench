package io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.business;

import io.github.quiethappiness.wrench.lua.manager.types.annotations.LuaScriptPath;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static io.github.quiethappiness.wrench.traffic.control.domain.service.IIdempotentAOP.JY_TRAFFIC_CONTROL_IDEMPOTENT;

/**
 * IIdempotentToken
 * @description
 * @author quietHappiness @jingyue
 * @date 2025/11/2 15:34
 * @version 1.0
 */
public interface IIdempotentToken
{
	
	String TOKEN_PREFIX = JY_TRAFFIC_CONTROL_IDEMPOTENT + ":token:";
	String RESULT_PREFIX = JY_TRAFFIC_CONTROL_IDEMPOTENT + ":result:";
	String RATE_LIMITER_PREFIX = JY_TRAFFIC_CONTROL_IDEMPOTENT + ":rate_limit:";
	Long TOKEN_EXPIRE_TIME = TimeUnit.HOURS.toSeconds(1);
	Long RESULT_EXPIRE_TIME = TimeUnit.HOURS.toSeconds(1);
	Duration RATE_LIMITER_DURATION = Duration.ofMinutes(1);
	Long DELETE_KEY_EXPIRE_TIME = TimeUnit.MINUTES.toSeconds(5);
	static String spliceTokenKey(String token)
	{
		return TOKEN_PREFIX + token;
	}
	 static String spliceResultKey(String token)
	{
		return RESULT_PREFIX + token;
	}
	 static String spliceRateLimiterKey(String businessType, String id)
	{
		return RATE_LIMITER_PREFIX + businessType + ":" + id;
	}
	static String generateUniqueToken()
	{
		return String.format("%d_%s",
			System.currentTimeMillis(),
			UUID
				.randomUUID()
				.toString()
				.substring(0, 8)
		);
	}
	
	String generateToken();
	
	String getTokenFromRequest(String headerName);
	
	@LuaScriptPath(fileFullPath = "script/idempotent/mark_token_used.lua")
	boolean checkAndMarkToken(String token);
	
	@LuaScriptPath(fileFullPath = "script/idempotent/cache_request_result.lua")
	void cacheResult(String token, Object result);
	
	<T> T getPreviousResult(String token, Class<T> clazz);
	
	@LuaScriptPath(fileFullPath = "script/idempotent/delete_token_and_result.lua")
	boolean preReleaseToken(String token);
	
	IdempotentTokenService.TokenInfo getTokenInfo(String token);
}