package io.github.quiethappiness.wrencher.trigger;

import io.github.quiethappiness.wrench.lua.manager.domain.service.manager.ILuaScriptManager;
import io.github.quiethappiness.wrench.method.extention.type.annotations.MeMethodExtension;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcHystrix;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcRateLimiter;
import io.github.quiethappiness.wrench.traffic.control.types.annotations.TcWhiteList;
import io.github.quiethappiness.wrench.traffic.control.types.enumvo.RateLimiterMode;
import io.github.quiethappiness.wrench.traffic.control.types.enumvo.WhiteListType;
import io.github.quiethappiness.wrencher.sample.IRedisWithLua;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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
// @EnableMethodExtension
public class IndexController
{
	@Resource
	private IRedisWithLua redisWithLua;
	
	@Resource
	private ILuaScriptManager luaScriptManager;
	@MeMethodExtension(beforeMethod = "before", beforeReturnJson = "{}", afterReturnMethod = "afterReturn", afterThrowingMethod = "afterThrowing", afterMethod = "after")
	@GetMapping(value = "method")
	public String methodExtension(String userId) throws InterruptedException
	{
		// Thread.sleep(2000);
		// throw new InterruptedException("test");
		return "test";
	}
	public String before(String userId) throws InterruptedException
	{
		log.info("before");
		// throw new InterruptedException("test");
		return "before";
	}
	public String after(String userId) throws InterruptedException
	{
		log.info("after");
		return "after";
	}
	public String afterReturn(String userId) throws InterruptedException
	{
		log.info("afterReturn");
		return "afterReturn";
	}
	public String afterThrowing(String userId) throws InterruptedException
	{
		log.info("afterThrowing");
		return "afterThrowing";
	}
	@TcHystrix(timeout = 100,returnJson = "", fallbackMethod = "drawHystrix")
	@GetMapping(value = "hystrix")
	public String HYSTRIX(String userId) throws InterruptedException
	{
		Thread.sleep(2000);
		return "test";
	}
	
	/**
	 * curl --request GET \
	 * --url 'http://127.0.0.1:9191/api/v1/index/draw?userId=xiaofuge'
	 */
	@TcWhiteList(key = "#{userId}", type = WhiteListType.USER_ID, fallbackMethod = "drawErrorRateLimiter")
	@GetMapping(value = "whitelist")
	public String WHITELIST(String userId)
	{
		return "test";
	}
	
	@TcWhiteList(key = "#{userId}", type = WhiteListType.USER_ID, fallbackMethod = "drawErrorRateLimiter")
	@TcRateLimiter(key = "userId", mode = RateLimiterMode.PPS_BLACKLIST, fallbackMethod = "drawErrorRateLimiter", permitsPerSecond = 1.0d, blacklistCount = 3)
	@GetMapping(value = "PPS_BLACKLIST")
	public String PPS_BLACKLIST(String userId)
	{
		return "test";
	}
	
	@TcRateLimiter(key = "userId", mode = RateLimiterMode.PPS, fallbackMethod = "drawErrorRateLimiter", permitsPerSecond = 1, blacklistCount = 3)
	@GetMapping(value = "PPS")
	public String PPS(String userId)
	{
		return "test";
	}
	
	@TcRateLimiter(key = "userId", mode = RateLimiterMode.SWR, fallbackMethod = "drawErrorRateLimiter", maxRequests = 2, blacklistCount = 3)
	@GetMapping(value = "SWR")
	public String SWR(String userId)
	{
		return "test";
	}
	
	@TcRateLimiter(key = "userId", mode = RateLimiterMode.SWR_BLACKLIST, fallbackMethod = "drawErrorRateLimiter", maxRequests = 2, blacklistCount = 2)
	@GetMapping(value = "SWR_BLACKLIST")
	public String SWR_BLACKLIST(String userId)
	{
		return "test";
	}
	
	public String drawWhiteList(String userId)
	{
		return "whiteList";
	}
	
	public String drawErrorRateLimiter(String userId)
	{
		return "rateLimiter";
	}
	
	public String drawHystrix(String userId)
	{
		return "hystrix";
	}
	
	@Scheduled(cron = "0/5 * * * * ?")
	public void test()
	{
		// System.out.println(luaScriptManager.getAllScriptInfo());
		redisWithLua.incr_with_ttl("test", 1, 10, TimeUnit.MINUTES);
		log.info("test");
	}
}