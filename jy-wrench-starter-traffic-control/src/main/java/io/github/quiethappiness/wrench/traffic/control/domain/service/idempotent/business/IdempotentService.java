package io.github.quiethappiness.wrench.traffic.control.domain.service.idempotent.business;

import com.alibaba.fastjson.JSON;
import io.github.quiethappiness.wrench.lua.manager.domain.model.valobj.ScriptNameContext;
import io.github.quiethappiness.wrench.lua.manager.domain.service.manager.ILuaScriptManager;
import io.github.quiethappiness.wrench.lua.manager.types.annotations.LuaScriptPath;
import io.github.quiethappiness.wrench.util.redisson.domain.base.impl.IRedisService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class IdempotentService implements IIdempotentToken
{
	
	@Resource
	private IRedisService redisService;
	
	@Resource
	private ILuaScriptManager luaScriptManager;
	@Resource
	protected HttpServletRequest request;
	
	@Override
	public String generateToken()
	{
		String token = IIdempotentToken.generateUniqueToken();
		String tokenKey = IIdempotentToken.spliceTokenKey(token);
		// 存储令牌，设置过期时间
		redisService.setValue(tokenKey, "VALID", 1, TimeUnit.HOURS);
		return token;
	}
	public String getTokenFromRequest(String headerName)
	{
		return request.getHeader(headerName);
	}
	public boolean hasSimilarRecentRequest(ProceedingJoinPoint joinPoint, String businessType)
	{
		// 实现相似请求检查
		// 可以根据方法参数生成指纹进行比较
		return false; // 示例
	}
	/**
	 * 检查并标记令牌为已使用（原子操作）
	 */
	@LuaScriptPath(fileFullPath = "script/idempotent/mark_token_used.lua")
	public boolean checkAndMarkToken(String token)
	{
		String tokenKey = IIdempotentToken.spliceTokenKey(token);
		String resultKey = IIdempotentToken.spliceResultKey(token);
		long currentTime = System.currentTimeMillis();
		// 1小时
		Object result = luaScriptManager.executeScript(
			ILuaScriptManager.LuaScriptExecuteVO
				.builder()
				.scriptName(ScriptNameContext.getScriptName())
				.mode(RScript.Mode.READ_WRITE)
				.returnType(RScript.ReturnType.VALUE)
				.keys(Arrays.asList(tokenKey, resultKey))
				.args(new String[] {String.valueOf(currentTime), String.valueOf(TOKEN_EXPIRE_TIME)})
				.build()
		);
		return "SUCCESS".equals(result);
	}
	
	/**
	 * 存储业务执行结果
	 */
	@LuaScriptPath(fileFullPath = "script/idempotent/cache_request_result.lua")
	public void cacheResult(String token, Object result)
	{
		String resultKey = IIdempotentToken.spliceResultKey(token);
		String resultJson = JSON.toJSONString(result);
		
		luaScriptManager.executeScript(
			ILuaScriptManager.LuaScriptExecuteVO
				.builder()
				.scriptName(ScriptNameContext.getScriptName())
				.mode(RScript.Mode.READ_WRITE)
				.returnType(RScript.ReturnType.STATUS)
				.keys(List.of(resultKey))
				.args(new String[] {resultJson, Long.toString(RESULT_EXPIRE_TIME)})
				.build()
		);
	}
	
	/**
	 * 获取之前执行的结果
	 */
	public <T> T getPreviousResult(String token, Class<T> clazz)
	{
		String resultKey = IIdempotentToken.spliceResultKey(token);
		RMap<String, String> resultMap = redisService.getMap(resultKey);
		String jsonResult = resultMap.get("result");
		if (jsonResult != null)
		{
			return JSON.parseObject(jsonResult, clazz);
		}
		return null;
	}
	
	/**
	 * 删除令牌及相关数据
	 */
	@LuaScriptPath(fileFullPath = "script/idempotent/delete_token_and_result.lua")
	public boolean deleteToken(String token)
	{
		Object result = luaScriptManager.executeScript(
			ILuaScriptManager.LuaScriptExecuteVO
				.builder()
				.scriptName(ScriptNameContext.getScriptName())
				.mode(RScript.Mode.READ_WRITE)
				.returnType(RScript.ReturnType.INTEGER)
				.keys(List.of(IIdempotentToken.spliceTokenKey(token), IIdempotentToken.spliceResultKey(token)))
				.args(new String[] {Long.toString(DELETE_KEY_EXPIRE_TIME)})
				.build()
		);
		
		return ((Long) result) > 0;
	}
	public boolean shouldReleaseToken(Exception e)
	{
		// 根据异常类型决定是否释放令牌
		// 比如网络异常、参数错误等可以释放，业务逻辑错误不释放
		return e instanceof NullPointerException ||
			e instanceof IllegalArgumentException;
	}
	/**
	 * 清理过期令牌（定时任务调用）
	 */
	public void cleanExpiredTokens()
	{
		// Redisson会自动处理过期的key
		// 如果需要额外的清理逻辑可以在这里实现
	}
	public boolean isRequestTooFrequent(String businessType, String id)
	{
		// 实现频率检查逻辑
		// 可以使用Redisson的RRateLimiter
		
		String key = IIdempotentToken.spliceRateLimiterKey(businessType, id);
		RRateLimiter rateLimiter = redisService.getRateLimiter(key);
		// 设置key的过期时间
		redisService.getBucket(key).expire(Duration.ofMinutes(RATE_LIMITER_DURATION.toMinutes()));
		rateLimiter.trySetRate(RateType.OVERALL, 4,RATE_LIMITER_DURATION ); // 每分钟10次
		return !rateLimiter.tryAcquire(1);
	}
	/**
	 * 获取令牌信息
	 */
	@Override
	public TokenInfo getTokenInfo(String token)
	{
		String tokenKey = IIdempotentToken.spliceTokenKey(token);
		String resultKey = IIdempotentToken.spliceResultKey(token);
		String tokenValue = redisService.getValue(tokenKey);
		RMap<String, String> resultMap = redisService.getMap(resultKey);
		TokenInfo info = new TokenInfo();
		info.setToken(token);
		info.setStatus(tokenValue);
		info.setUsedTime(resultMap.get("used_time"));
		info.setResult(resultMap.get("result"));
		return info;
	}
	
	@Data
	public static class TokenInfo
	{
		private String token;
		private String status; // VALID, USED
		private String usedTime;
		private String result;
	}
}