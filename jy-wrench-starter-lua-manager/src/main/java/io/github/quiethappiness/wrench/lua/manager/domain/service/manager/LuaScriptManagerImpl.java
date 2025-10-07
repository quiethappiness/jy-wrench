package io.github.quiethappiness.wrench.lua.manager.domain.service.manager;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * LuaScriptManagerImpl
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description lua脚本管理器
 * @date 2025/9/9 16:08
 */

@Slf4j
@Service
public class LuaScriptManagerImpl extends AbstractLuaScriptManager
{
	private final RedissonClient redissonClient;
	
	{
		log.info("luaScriptManager 正在初始化...");
	}
	
	public LuaScriptManagerImpl(
		@Autowired RedissonClient redissonClient)
	{
		this.redissonClient = redissonClient;
	}
	
	/**
	 * 每分钟检查脚本更新，避免高频日志输出
	 */
	@Scheduled(fixedRate = 60000)
	public void checkScriptUpdates()
	{
		log.debug("Starting scheduled script update check...");
		for (Map.Entry<String, LuaScriptVO> entry : uriToVOMap.entrySet())
		{
			String scriptName = entry.getKey();
			LuaScriptVO vo = entry.getValue();
			try
			{
				ClassPathResource resource = new ClassPathResource(vo.getPath());
				if (!resource.exists())
				{
					log.warn("Script file does not exist: {}", vo.getPath());
					continue;
				}
				long currentModified = resource.lastModified();
				long lastModified = vo.getLastModified();
				if (currentModified > lastModified)
				{
					log.info("Script changed detected: '{}' ({} -> {}), triggering reload...",
						scriptName, new Date(lastModified), new Date(currentModified));
					log.info("Reloading script: {}", scriptName);
					super.loadSingleScript(scriptName);
					this.preloadScript(scriptName);
				}
			}
			catch (IOException e)
			{
				log.error("Failed to check last modified time for script: {}", scriptName, e);
			}
		}
	}
	
	/**
	 * 预加载单个脚本到Redis
	 * @param scriptName
	 * 	脚本名称
	 */
	@Override
	public void preloadScript(String scriptName)
	{
		LuaScriptVO vo = uriToVOMap.get(scriptName);
		if (vo == null)
		{
			log.error("Script not registered: {}", scriptName);
			return;
		}
		String content = vo.getCache();
		if (content == null)
		{
			log.error("Script content not loaded: {}", scriptName);
			return;
		}
		try
		{
			String sha = redissonClient.getScript()
				.scriptLoad(content);
			vo.setSha(sha);
			log.info("Preloaded script: {}, SHA: {}", scriptName, sha);
		}
		catch (Exception e)
		{
			log.error("Failed to preload script: {}", scriptName, e);
		}
	}
	
	/**
	 * 执行脚本
	 * @param name
	 * 	脚本名称
	 * @param mode
	 * 	执行模式
	 * @param returnType
	 * 	返回类型
	 * @param keys
	 * 	键列表
	 * @param args
	 * 	参数列表
	 * @return 脚本执行结果
	 */
	@Override
	public Object executeScript(
		String name, RScript.Mode mode, RScript.ReturnType returnType,
		List<Object> keys, String... args)
	{
		// 添加详细的调试日志
		log.info("Executing script: {}, Keys: {}, Args: {}", name, keys, Arrays.toString(args));
		String scriptName = nameToURIMap.get(name);
		LuaScriptVO vo = uriToVOMap.get(scriptName);
		String sha = vo.getSha();
		if (sha == null)
		{
			log.error("Script SHA not found: {}", name);
			throw new IllegalArgumentException("Script not preloaded: " + name);
		}
		// 确保参数都是字符串类型，避免二进制缓冲区直接传递
		Object[] processedArgs = new Object[args.length];
		for (int i = 0; i < args.length; i++)
		{
			if (args[i] != null)
			{
				processedArgs[i] = args[i].toString();
			}
			else
			{
				processedArgs[i] = "";
			}
		}
		try
		{
			Object result = redissonClient.getScript()
				.evalSha(
					mode,
					sha,
					returnType,
					keys,
					processedArgs
				);
			log.debug("Script executed successfully: {}, Result: {}", name, result);
			return result;
		}
		catch (RedisException e)
		{
			// 添加更详细的错误日志
			log.error("Redis script execution failed: {}. Keys: {}, Args: {}", name, keys, Arrays.toString(args), e);
			// 更安全地判断 NOSCRIPT，防止 getMessage() 为 null
			String msg = e.getMessage();
			if (msg != null && msg.contains("NOSCRIPT"))
			{
				log.warn("Script not found in Redis (NOSCRIPT): {}, attempting reload...", name);
				scriptName = nameToURIMap.get(name);
				preloadScript(scriptName);
				sha = uriToVOMap.get(scriptName)
					.getSha();
				if (sha == null)
				{
					log.error("Failed to reload script SHA after NOSCRIPT error: {}", name);
					throw e;
				}
				Object result = redissonClient.getScript()
					.evalSha(mode, sha, returnType, keys, processedArgs);
				log.debug("Script reloaded and executed successfully after retry: {}, Result: {}", name, result);
				return result;
			}
			else
			{
				log.error("Non-NOSCRIPT Redis error occurred during script execution: {}", name, e);
				throw e;
			}
		}
	}
}