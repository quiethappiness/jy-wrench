package io.github.quiethappiness.wrench.dynamic.config.center.config;

import io.github.quiethappiness.wrench.dynamic.config.center.domain.model.valobj.AttributeVO;
import io.github.quiethappiness.wrench.dynamic.config.center.domain.listener.DynamicConfigCenterAdjustListener;
import io.github.quiethappiness.wrench.dynamic.config.center.types.common.Constants;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * DynamicConfigCenterRegisterAutoConfig
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 动态配置-注册
 * @date 2025/9/10 16:31
 */
@AutoConfiguration
@EnableConfigurationProperties(value = {DynamicConfigCenterRegisterAutoProperties.class, DynamicConfigCenterAutoProperties.class})
@ComponentScan("io.github.quiethappiness.wrench.dynamic.config.center.domain")
public class DynamicConfigCenterRegisterAutoConfig
{
	private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterRegisterAutoConfig.class);
	
	@Bean("jyWrenchRedissonClient")
	public RedissonClient jyWrenchRedissonClient(
		DynamicConfigCenterRegisterAutoProperties properties
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
	// public IDynamicConfigCenterService dynamicConfigCenterService(
	// 	DynamicConfigCenterAutoProperties dynamicConfigCenterAutoProperties,
	// 	RedissonClient jyWrenchRedissonClient)
	// {
	//
	// 	return new DynamicConfigCenterServiceImpl(dynamicConfigCenterAutoProperties, jyWrenchRedissonClient);
	// }
	
	
	// @Bean
	// public DynamicConfigCenterAdjustListener dynamicConfigCenterAdjustListener(
	// 	IDynamicConfigCenterService dynamicConfigCenterService)
	// {
	//
	// 	return new DynamicConfigCenterAdjustListener(dynamicConfigCenterService);
	// }
	
	@Bean
	public RTopic dynamicConfigCenterTopic(
		DynamicConfigCenterAutoProperties dynamicConfigCenterAutoProperties,
		RedissonClient jyWrenchRedissonClient,
		DynamicConfigCenterAdjustListener dynamicConfigCenterAdjustListener)
	{
		RTopic dynamicConfigCenterTopic = jyWrenchRedissonClient.getTopic(
			Constants.getTopic(dynamicConfigCenterAutoProperties.getSystem()));
		dynamicConfigCenterTopic.addListener(AttributeVO.class, dynamicConfigCenterAdjustListener);
		log.info("jy-wrench，注册器（redis）Topic dynamicConfigCenterTopic 创建完成。");
		return dynamicConfigCenterTopic;
	}
}