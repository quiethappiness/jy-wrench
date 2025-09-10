package com.jingyue.wrench.dynamic.config.center.config;

import com.jingyue.wrench.dynamic.config.center.domain.model.valobj.AttributeVO;
import com.jingyue.wrench.dynamic.config.center.domain.service.DynamicConfigCenterServiceImpl;
import com.jingyue.wrench.dynamic.config.center.domain.service.IDynamicConfigCenterService;
import com.jingyue.wrench.dynamic.config.center.listener.DynamicConfigCenterAdjustListener;
import com.jingyue.wrench.dynamic.config.center.types.common.Constants;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DynamicConfigCenterRegisterAutoConfig
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 动态配置-注册
 * @date 2025/9/10 16:31
 */
@Configuration
@EnableConfigurationProperties(value = {DynamicConfigCenterRegisterAutoProperties.class, DynamicConfigCenterAutoProperties.class})
public class DynamicConfigCenterRegisterAutoConfig
{
	private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterRegisterAutoConfig.class);
	
	@Bean("jyWrenchRedissonClient")
	public RedissonClient redissonClient(DynamicConfigCenterRegisterAutoProperties properties)
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
		log.debug("jy-wrench，注册器（redis）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());
		return redissonClient;
	}
	
	@Bean
	public IDynamicConfigCenterService dynamicConfigCenterService(
		DynamicConfigCenterAutoProperties dynamicConfigCenterAutoProperties,
		RedissonClient jyWrenchRedissonClient)
	{
		log.debug("jy-wrench，注册器（redis）服务 dynamicConfigCenterService 初始化完成。");
		return new DynamicConfigCenterServiceImpl(dynamicConfigCenterAutoProperties, jyWrenchRedissonClient);
	}
	
	
	@Bean
	public DynamicConfigCenterAdjustListener dynamicConfigCenterAdjustListener(
		IDynamicConfigCenterService dynamicConfigCenterService)
	{
		log.debug("jy-wrench，注册器（redis）监听器 dynamicConfigCenterAdjustListener 初始化完成。");
		return new DynamicConfigCenterAdjustListener(dynamicConfigCenterService);
	}
	
	@Bean
	public RTopic dynamicConfigCenterTopic(
		DynamicConfigCenterAutoProperties dynamicConfigCenterAutoProperties,
		RedissonClient jyWrenchRedissonClient,
		DynamicConfigCenterAdjustListener dynamicConfigCenterAdjustListener)
	{
		RTopic dynamicConfigCenterTopic = jyWrenchRedissonClient.getTopic(
			Constants.getTopic(dynamicConfigCenterAutoProperties.getSystem()));
		dynamicConfigCenterTopic.addListener(AttributeVO.class, dynamicConfigCenterAdjustListener);
		log.debug("jy-wrench，注册器（redis）Topic dynamicConfigCenterTopic 创建完成。");
		return dynamicConfigCenterTopic;
	}
}