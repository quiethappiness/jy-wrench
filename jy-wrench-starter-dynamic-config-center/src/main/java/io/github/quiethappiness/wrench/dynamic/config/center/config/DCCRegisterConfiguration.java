package io.github.quiethappiness.wrench.dynamic.config.center.config;

import io.github.quiethappiness.wrench.dynamic.config.center.domain.listener.DCCAdjustListener;
import io.github.quiethappiness.wrench.dynamic.config.center.domain.model.valobj.AttributeVO;
import io.github.quiethappiness.wrench.dynamic.config.center.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * DCCAutoConfiguration
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 动态配置-注册
 * @date 2025/9/10 16:31
 */
@Configuration
@EnableConfigurationProperties(value = {DCCRegisterAutoProperties.class, DCCAutoProperties.class})
@ComponentScan("io.github.quiethappiness.wrench.dynamic.config.center.domain")
@Slf4j
public class DCCRegisterConfiguration
{
	@Bean("jyWrenchRedissonClient")
	public RedissonClient jyWrenchRedissonClient(
		DCCRegisterAutoProperties properties
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
		log.info("jy-wrench，注册器（redis）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());
		return redissonClient;
	}
	
	// @Bean
	// public IDCCService dynamicConfigCenterService(
	// 	DCCAutoProperties dynamicConfigCenterAutoProperties,
	// 	RedissonClient jyWrenchRedissonClient)
	// {
	//
	// 	return new DCCServiceImpl(dynamicConfigCenterAutoProperties, jyWrenchRedissonClient);
	// }
	
	
	// @Bean
	// public DCCAdjustListener dynamicConfigCenterAdjustListener(
	// 	IDCCService dynamicConfigCenterService)
	// {
	//
	// 	return new DCCAdjustListener(dynamicConfigCenterService);
	// }
	
	@Bean
	public RTopic dynamicConfigCenterTopic(
		DCCAutoProperties DCCAutoProperties,
		RedissonClient jyWrenchRedissonClient,
		DCCAdjustListener DCCAdjustListener)
	{
		RTopic dynamicConfigCenterTopic = jyWrenchRedissonClient.getTopic(
			Constants.getTopic(DCCAutoProperties.getSystem()));
		dynamicConfigCenterTopic.addListener(AttributeVO.class, DCCAdjustListener);
		log.info("jy-wrench，注册器（redis）Topic dynamicConfigCenterTopic 创建完成。");
		return dynamicConfigCenterTopic;
	}
}