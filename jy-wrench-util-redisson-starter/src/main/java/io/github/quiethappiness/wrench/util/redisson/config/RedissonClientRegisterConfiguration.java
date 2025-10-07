package io.github.quiethappiness.wrench.util.redisson.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DCCAutoConfiguration
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 动态配置-注册
 * @date 2025/9/10 16:31
 */
@Configuration
@EnableConfigurationProperties(value = {RedissonClientRegisterProperties.class})
@Slf4j
public class RedissonClientRegisterConfiguration
{
	@Bean("redissonClient")
	public RedissonClient redissonClient(
		RedissonClientRegisterProperties properties
	)
	{
		Config config = new Config();
		// 根据需要可以设定编解码器；https://github.com/redisson/redisson/wiki/4.-%E6%95%B0%E6%8D%AE%E5%BA%8F%E5%88%97%E5%8C%96
		config.setCodec(JsonJacksonCodec.INSTANCE);
		config.useSingleServer()
			.setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
			.setPassword(properties.getPassword())
			.setConnectionPoolSize(properties.getPoolSize())
			.setConnectionMinimumIdleSize(properties.getMinIdleSize())
			.setIdleConnectionTimeout(properties.getIdleTimeout())
			.setConnectTimeout(properties.getConnectTimeout())
			.setRetryAttempts(properties.getRetryAttempts())
			.setRetryInterval(properties.getRetryInterval())
			.setPingConnectionInterval(properties.getPingInterval())
			.setKeepAlive(properties.isKeepAlive())
		;
		RedissonClient redissonClient = Redisson.create(config);
		log.info("jy-wrench，注册器（RedissonClient）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());
		return redissonClient;
	}
}