package io.github.quiethappiness.wrench.lua.manager.domain.model.valobj;

public class ScriptNameContext
{
	private static final ThreadLocal<String> SCRIPT_NAME = new ThreadLocal<>();
	
	public static void setScriptName(String scriptName) {
		SCRIPT_NAME.set(scriptName);
	}
	
	public static String getScriptName() {
		return SCRIPT_NAME.get();
	}
	
	public static void clear() {
		SCRIPT_NAME.remove();
	}
}
