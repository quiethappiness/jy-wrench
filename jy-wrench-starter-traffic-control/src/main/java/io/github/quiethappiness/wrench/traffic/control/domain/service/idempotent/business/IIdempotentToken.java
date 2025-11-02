package io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.business;

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
	
	IdempotentService.TokenInfo getTokenInfo(String token);
}