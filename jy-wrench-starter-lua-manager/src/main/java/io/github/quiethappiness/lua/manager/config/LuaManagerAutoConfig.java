package io.github.quiethappiness.lua.manager.config;

import io.github.quiethappiness.lua.manager.domain.service.aop.LuaScriptServiceAop;
import io.github.quiethappiness.lua.manager.domain.service.manager.ILuaScriptManager;
import io.github.quiethappiness.lua.manager.domain.service.manager.LuaScriptManagerImpl;
import io.github.quiethappiness.lua.manager.domain.service.base.impl.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableConfigurationProperties(value = {LuaManagerAutoProperties.class})
@EnableAspectJAutoProxy
// @ComponentScan(basePackages = {"io.github.quiethappiness.lua.manager"})
@Slf4j
public class LuaManagerAutoConfig
{
	@Bean
	public ILuaScriptManager luaScriptManager(
		@Autowired RedissonClient jyWrenchRedissonClient, @Autowired LuaManagerAutoProperties luaManagerAutoProperties)
	{
		log.info("luaScriptManager 正在初始化...");
		return new LuaScriptManagerImpl(jyWrenchRedissonClient, luaManagerAutoProperties);
	}
	
	@Bean
	public LuaBeanPostProcessor luaBeanPostProcessor(
		ILuaScriptManager luaScriptManager
	)
	{
		log.info("LuaBeanPostProcessor 正在初始化...");
		return new LuaBeanPostProcessor(luaScriptManager);
	}
	
	@Bean
	public LuaScriptServiceAop luaScriptServiceAop()
	{
		log.info("LuaScriptServiceAop 正在初始化...");
		return new LuaScriptServiceAop();
	}
	
	@Bean
	public IRedisService redisService(
		RedissonClient jyWrenchRedissonClient)
	{
		log.info("redisService 正在初始化...");
		return IRedisService.defaultRedisService(jyWrenchRedissonClient);
	}
}
