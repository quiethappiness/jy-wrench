package io.github.quiethappiness.wrench.util.redisson.config;

import io.github.quiethappiness.wrench.util.redisson.domain.impl.RedissonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RedissonService.class})
@Slf4j
public class RedissonUtilAutoConfiguration
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