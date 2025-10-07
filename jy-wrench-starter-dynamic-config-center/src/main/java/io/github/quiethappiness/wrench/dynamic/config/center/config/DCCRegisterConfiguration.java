package io.github.quiethappiness.wrench.dynamic.config.center.config;

import io.github.quiethappiness.wrench.dynamic.config.center.domain.listener.DCCAdjustListener;
import io.github.quiethappiness.wrench.dynamic.config.center.domain.model.valobj.AttributeVO;
import io.github.quiethappiness.wrench.dynamic.config.center.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
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
@EnableConfigurationProperties(value = {DCCAutoProperties.class})
@ComponentScan("io.github.quiethappiness.wrench.dynamic.config.center.domain")
@Slf4j
public class DCCRegisterConfiguration
{
	
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
		RedissonClient redissonClient,
		DCCAdjustListener DCCAdjustListener)
	{
		RTopic dynamicConfigCenterTopic = redissonClient.getTopic(
			Constants.getTopic(DCCAutoProperties.getSystem()));
		dynamicConfigCenterTopic.addListener(AttributeVO.class, DCCAdjustListener);
		log.info("jy-wrench，注册器（redis）Topic dynamicConfigCenterTopic 创建完成。");
		return dynamicConfigCenterTopic;
	}
}