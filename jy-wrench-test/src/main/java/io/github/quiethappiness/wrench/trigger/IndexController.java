package io.github.quiethappiness.wrench.trigger;

import io.github.quiethappiness.lua.manager.domain.service.manager.ILuaScriptManager;
import io.github.quiethappiness.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor;
import io.github.quiethappiness.wrench.rate.limiter.types.enumvo.RateLimitMode;
import io.github.quiethappiness.wrench.sample.IRedisWithLua;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author quiethappiness @jignyue
 * 	2025-05-07 14:41
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/index/")
public class IndexController
{
	@Resource
	private IRedisWithLua redisWithLua;
	
	@Resource
	private ILuaScriptManager luaScriptManager;
	/**
	 * curl --request GET \
	 * --url 'http://127.0.0.1:9191/api/v1/index/draw?userId=xiaofuge'
	 */
	@RateLimiterAccessInterceptor(key = "userId", mode = RateLimitMode.PPS_BLACKLIST, fallbackMethod = "drawErrorRateLimiter", permitsPerSecond = 1.0d, blacklistCount = 3)
	@GetMapping(value = "PPS_BLACKLIST")
	public String PPS_BLACKLIST(String userId)
	{
		return "test";
	}
	@RateLimiterAccessInterceptor(key = "userId", mode = RateLimitMode.PPS, fallbackMethod = "drawErrorRateLimiter", permitsPerSecond = 1, blacklistCount = 3)
	@GetMapping(value = "PPS")
	public String PPS(String userId)
	{
		return "test";
	}
	@RateLimiterAccessInterceptor(key = "userId", mode = RateLimitMode.SWR, fallbackMethod = "drawErrorRateLimiter", maxRequests = 2, blacklistCount = 3)
	@GetMapping(value = "SWR")
	public String SWR(String userId)
	{
		return "test";
	}
	@RateLimiterAccessInterceptor(key = "userId", mode = RateLimitMode.SWR_BLACKLIST, fallbackMethod = "drawErrorRateLimiter", maxRequests = 2, blacklistCount = 2)
	@GetMapping(value = "SWR_BLACKLIST")
	public String SWR_BLACKLIST(String userId)
	{
		return "test";
	}
	
	public String drawErrorRateLimiter(String userId)
	{
		return "rateLimiter";
	}
	
	// @Scheduled(cron = "0/5 * * * * ?")
	public void test()
	{
		System.out.println(luaScriptManager.getAllScriptInfo());
		redisWithLua.incrWithTtl("test", 1, 10, TimeUnit.MINUTES);
		log.info("test");
	}
}