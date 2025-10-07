package io.github.quiethappiness.wrench.lua.manager.domain.service.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.NonNull;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractLuaScriptManager implements ILuaScriptManager
{
	@Value("${jy.wrench.config.lua.path:script}")
	protected String scriptPath;
	public static final String LOCAL_SEPARATOR = "/";
	// 脚本缓存：脚本名称 -> 脚本内容
	protected final Map<String, LuaScriptVO> uriToVOMap = new ConcurrentHashMap<>();
	
	/**
	 * 自动扫描resources/script目录下的lua文件并注册
	 */
	protected void scanAndRegisterScriptsToMapPaths(String folderPath, String version)
	{
		try
		{
			ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resolver.getResources("classpath*:" + spliceLuaFilePath(scriptPath, folderPath,null));
			Arrays.stream(resources)
				.forEach(resource -> registerSingleScriptToMapPaths(resource, version));
			log.info("Auto-registered {} lua scripts", resources.length);
		}
		catch (Exception e)
		{
			log.error("Failed to scan lua scripts", e);
		}
	}
	
	public static String spliceLuaFilePath(@NonNull String scriptPath,@NonNull String folderPath, String filename)
	{
		if (!StringUtils.hasText(filename))
		{
			return spliceLuaFilePath(scriptPath, folderPath, "*");
		}
		return scriptPath + LOCAL_SEPARATOR + folderPath + LOCAL_SEPARATOR + filename + ".lua";
	}
	
	/**
	 * 注册脚本（不加载内容）
	 * @param resource
	 * 	资源路径
	 * @param version
	 * 	版本号
	 */
	@Override
	public String registerSingleScriptToMapPaths(Resource resource, String version)
	{
		try
		{
			String filename = resource.getFilename();
			if (filename != null && filename.endsWith(".lua"))
			{
				// // 从路径中提取脚本名称（去除.lua扩展名）
				String name = filename.substring(0, filename.length() - 4);
				// // 提取"classpath:"后的路径部分
				String uri = resource.getURI()
					.toString();
				// // 获取资源的相对路径
				String path = uri.substring(uri.indexOf(scriptPath + LOCAL_SEPARATOR));
				String scriptName = path.substring(0, path.lastIndexOf(".lua"));
				// 注册脚本，默认版本号为1.0
				// 从路径中提取脚本名称（包含文件夹路径）
				uriToVOMap.put(scriptName, LuaScriptVO.builder()
					.name(name)
					.version(version)
					.path(path)
					.build());
				log.info("Registered script: {} (v{}) at {}", name, version, path);
				return scriptName;
			}
		}
		catch (Exception e)
		{
			log.error("Failed to register script: {}", resource.getFilename(), e);
		}
		return null;
	}
	
	/**
	 * 加载所有已注册脚本
	 */
	protected void loadAllScriptsFromMapPathsToMapCache(String folder)
	{
		String pattern = scriptPath + LOCAL_SEPARATOR + folder + LOCAL_SEPARATOR;
		uriToVOMap.keySet()
			.stream()
			.filter(s ->
			{
				log.warn("key: {},pattern: {}", s, pattern);
				return s.startsWith(pattern);
			})
			.forEach(this::loadSingleScript);
	}
	
	/**
	 * 加载单个脚本
	 * @param scriptName
	 * 	脚本名称
	 */
	protected void loadSingleScript(String scriptName)
	{
		LuaScriptVO vo = uriToVOMap.get(scriptName);
		if (vo == null)
		{
			log.error("Script not registered: {}", scriptName);
			return;
		}
		String path = vo.getPath();
		if (path == null)
		{
			log.error("Script not registered: {}", scriptName);
			return;
		}
		try
		{
			log.info("Loaded script...: {} ({})", scriptName, vo.getVersion());
			ClassPathResource resource = new ClassPathResource(path);
			if (!resource.exists())
			{
				log.error("Script resource not found: {}", path);
				return;
			}
			// 显式使用 try-with-resources 确保流关闭
			try ( InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8) )
			{
				String content = FileCopyUtils.copyToString(reader);
				// 更新缓存
				// scriptCache.put(scriptName, content);
				vo.setCache(content);
				long lastModified = resource.lastModified(); // 已确认 exists()
				vo.setLastModified(lastModified);
			}
			log.info("Loaded script successfully: {} ({})", scriptName, vo.getVersion());
		}
		catch (IOException e)
		{
			log.error("Failed to load script: {}", scriptName, e);
		}
	}
	
	/**
	 * 预加载所有脚本到Redis
	 */
	protected void preloadScriptsFromMapCacheToRedis(String folder)
	{
		String pattern = scriptPath + LOCAL_SEPARATOR + folder + LOCAL_SEPARATOR;
		uriToVOMap.keySet()
			.stream()
			.filter(s -> s.startsWith(pattern))
			.forEach(this::preloadScript);
	}
	
	public abstract void preloadScript(String scriptName);
	
	/**
	 * 获取脚本版本
	 * @param scriptName
	 * 	脚本名称
	 * @return 版本号
	 */
	@Override
	public String getScriptVersion(String scriptName)
	{
		return uriToVOMap.get(scriptName)
			.getVersion();
	}
	
	/**
	 * 获取脚本SHA值
	 * @param scriptName
	 * 	脚本名称
	 * @return SHA值
	 */
	@Override
	public String getScriptSHA(String scriptName)
	{
		return uriToVOMap.get(scriptName)
			.getSha();
	}
	
	/**
	 * 获取所有脚本的详细信息，用于监控和调试
	 * @return Map<脚本名, Map < 属性, 值>>
	 */
	@Override
	public Map<String, LuaScriptVO> getAllScriptInfo()
	{
		return uriToVOMap;
	}
}