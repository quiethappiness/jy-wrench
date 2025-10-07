package io.github.quiethappiness.wrench.lua.manager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAspectJAutoProxy
@Import({LuaManagerConfiguration.class})
@Slf4j
public class LuaManagerAutoConfiguration
{
	// @Bean
	// public ILuaScriptManager luaScriptManager(
	// 	@Autowired RedissonClient jyWrenchRedissonClient, @Autowired LuaManagerAutoProperties luaManagerAutoProperties)
	// {
	// 	log.info("luaScriptManager 正在初始化...");
	// 	return new LuaScriptManagerImpl(jyWrenchRedissonClient, luaManagerAutoProperties);
	// }
	
	// @Bean
	// public LuaBeanPostProcessor luaBeanPostProcessor(
	// 	ILuaScriptManager luaScriptManager
	// )
	// {
	// 	log.info("LuaBeanPostProcessor 正在初始化...");
	// 	return new LuaBeanPostProcessor(luaScriptManager);
	// }
	
	// @Bean
	// public LuaScriptServiceAop luaScriptServiceAop()
	// {
	// 	log.info("LuaScriptServiceAop 正在初始化...");
	// 	return new LuaScriptServiceAop();
	// }
	
	// @Bean
	// public IRedisService redisService(
	// 	RedissonClient jyWrenchRedissonClient)
	// {
	// 	log.info("redisService 正在初始化...");
	// 	return IRedisService.defaultRedisService(jyWrenchRedissonClient);
	// }
}