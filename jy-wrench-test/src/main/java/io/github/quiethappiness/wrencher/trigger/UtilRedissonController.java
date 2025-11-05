package io.github.quiethappiness.wrencher.trigger;

import io.github.quiethappiness.wrench.traffic.control.types.annotations.UniqueIdentifier;
import io.github.quiethappiness.wrench.util.redisson.types.annotations.LockAndGet;
import io.github.quiethappiness.wrencher.interfaces.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * UtilRedissonController
 * @description redisson工具类模块测试
 * @author quietHappiness @jingyue
 * @date 2025/11/5 13:48
 * @version 1.0
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/util/redisson/")
public class UtilRedissonController
{
	@GetMapping(value = "lock")
	@LockAndGet(lockKey = "#{user.code}")
	public Map<String, Object> lockAndGet(
		@RequestBody @UniqueIdentifier(fieldPathsOrExpressions = {"#code", "#info"}) UserInfo user)
	throws InterruptedException
	{
		// Thread.sleep(2000);
		// throw new InterruptedException("test");
		HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("code", user.getCode());
		hashMap.put("info", user.getInfo());
		return hashMap;
	}
}