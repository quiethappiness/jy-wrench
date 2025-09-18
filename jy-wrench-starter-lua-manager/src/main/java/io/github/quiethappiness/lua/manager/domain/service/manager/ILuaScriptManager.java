package io.github.quiethappiness.lua.manager.domain.service.manager;

import io.github.quiethappiness.lua.manager.domain.model.valobj.LuaScriptVO;
import org.redisson.api.RScript;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

/**
 * ILuaScriptManager
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 脚本管理器接口
 * @date 2025/9/9 17:03
 */
public interface ILuaScriptManager
{
	
	void scanAndRegisterScriptsToMapPaths(String folderPath, String version);
	
	Object executeScript(
		String scriptName, RScript.Mode mode, RScript.ReturnType returnType,
		List<Object> keys, String... args);
	
	
	String registerSingleScriptToMapPaths(Resource resource, String version);
	
	String getScriptVersion(String scriptName);
	
	String getScriptSHA(String scriptName);
	
	void initScripts(String folderPath, String version);
	
	Map<String, LuaScriptVO> getAllScriptInfo();
	
	void initSingleScript(Resource resource, String version);
}