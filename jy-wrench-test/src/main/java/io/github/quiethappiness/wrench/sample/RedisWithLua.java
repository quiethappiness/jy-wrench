package io.github.quiethappiness.wrench.sample;

import io.github.quiethappiness.lua.manager.domain.service.manager.ILuaScriptManager;
import io.github.quiethappiness.lua.manager.types.annotations.LuaScriptPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * RedisWithLua
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 自己写的实现
 * @date 2025/9/9 15:34
 */
@Component
@Slf4j
@RequiredArgsConstructor
// @LuaScriptPath(folderPath = "common", version = "1.0")
public class RedisWithLua implements IRedisWithLua
{
	private final ILuaScriptManager scriptManager;
	
	@Override
	@LuaScriptPath(fileFullPath = "script/common/incr_with_ttl.lua", version = "1.0")
	public long incrWithTtl(String key, long delta, long ttl, @NonNull TimeUnit unit)
	{
		long seconds = unit.toSeconds(ttl);
		log.info("incr_with_ttl key: {}, delta: {}, ttl: {},seconds: {}", key, delta, ttl, seconds);
		return (Long) scriptManager.executeScript(
			"incr_with_ttl",
			RScript.Mode.READ_WRITE,
			RScript.ReturnType.INTEGER,
			Collections.singletonList(key),
			String.valueOf(delta), String.valueOf(seconds)
		);
	}
	
	@Override
	// @LuaScriptPath(fileFullPath = "script/setWithTtl.lua", version = "1.0")
	public void setWithTtl(String key, long value, long ttl, @NonNull TimeUnit unit)
	{
		long seconds = unit.toSeconds(ttl);
		log.info("set_with_ttl key: {}, value: {}, ttl: {}, seconds: {}", key, value, ttl, seconds);
		scriptManager.executeScript(
			"set_with_ttl",
			RScript.Mode.READ_WRITE,
			RScript.ReturnType.STATUS,
			Collections.singletonList(key),
			String.valueOf(value), String.valueOf( seconds)
		);
	}
}