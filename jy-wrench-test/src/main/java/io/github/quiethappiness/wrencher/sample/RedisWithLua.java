package io.github.quiethappiness.wrencher.sample;

import io.github.quiethappiness.wrench.lua.manager.domain.model.valobj.ScriptNameContext;
import io.github.quiethappiness.wrench.lua.manager.domain.service.manager.ILuaScriptManager;
import io.github.quiethappiness.wrench.lua.manager.types.annotations.LuaScriptPath;
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
@LuaScriptPath(folderPath = "common", version = "1.0")
public class RedisWithLua implements IRedisWithLua
{
	private final ILuaScriptManager scriptManager;
	
	@Override
	// @LuaScriptPath(fileFullPath = "script/common/incr_with_ttl.lua", version = "1.0")
	public long incr_with_ttl(String key, long delta, long ttl, @NonNull TimeUnit unit)
	{
		long seconds = unit.toSeconds(ttl);
		log.info("incr_with_ttl key: {}, delta: {}, ttl: {},seconds: {}", key, delta, ttl, seconds);
		return (Long) scriptManager.executeScript(
			ILuaScriptManager.LuaScriptExecuteVO.builder()
				.scriptName(ScriptNameContext.getScriptName())
				.mode(RScript.Mode.READ_WRITE)
				.returnType(RScript.ReturnType.INTEGER)
				.keys(Collections.singletonList(key))
				.args(new String[] {String.valueOf(delta), String.valueOf(seconds)})
				.build()
		);
	}
	
	@Override
	@LuaScriptPath(fileFullPath = "script/common/set_with_ttl.lua", version = "1.0")
	public void setWithTtl(String key, long value, long ttl, @NonNull TimeUnit unit)
	{
		long seconds = unit.toSeconds(ttl);
		log.info("set_with_ttl key: {}, value: {}, ttl: {}, seconds: {}", key, value, ttl, seconds);
		scriptManager.executeScript(
			ILuaScriptManager.LuaScriptExecuteVO.builder()
				.scriptName(ScriptNameContext.getScriptName())
				.mode(RScript.Mode.READ_WRITE)
				.returnType(RScript.ReturnType.STATUS)
				.keys(Collections.singletonList(key))
				.args(new String[] {String.valueOf(value), String.valueOf(seconds)})
				.build()
		);
	}
}