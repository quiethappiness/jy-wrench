package io.github.quiethappiness.wrench.dynamic.config.center.config;

import io.github.quiethappiness.wrench.dynamic.config.center.domain.service.IDCCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * DCCBeanPostProcessor
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 动态配置-注册
 * @date 2025/9/10 15:39
 */
@AutoConfiguration
public class DCCBeanPostProcessor implements BeanPostProcessor
{
	private final Logger log = LoggerFactory.getLogger(DCCBeanPostProcessor.class);
	private final IDCCService dynamicConfigCenterService;
	
	public DCCBeanPostProcessor(
		IDCCService dynamicConfigCenterService)
	{
		this.dynamicConfigCenterService = dynamicConfigCenterService;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
	{
		log.debug("jy-wrench，动态配置-注册，代理对象创建完成。");
		return dynamicConfigCenterService.proxyObject(bean);
	}
}