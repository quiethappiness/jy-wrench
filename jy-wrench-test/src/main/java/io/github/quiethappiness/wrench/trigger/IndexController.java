package io.github.quiethappiness.wrench.trigger;

import io.github.quiethappiness.wrench.rate.limiter.types.annotations.RateLimiterAccessInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 	2025-05-07 14:41
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/index/")
public class IndexController
{
	
	/**
	 * curl --request GET \
	 * --url 'http://127.0.0.1:9191/api/v1/index/draw?userId=xiaofuge'
	 */
	@RateLimiterAccessInterceptor(key = "userId", fallbackMethod = "drawErrorRateLimiter", permitsPerSecond = 1.0d, blacklistCount = 3)
	@GetMapping(value = "draw")
	public String draw(String userId)
	{
		return "test";
	}
	
	public String drawErrorRateLimiter(String userId)
	{
		return "rateLimiter";
	}
}