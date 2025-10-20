package io.github.quiethappiness.wrench.dynamic.config.center.config;

import io.github.quiethappiness.wrench.dynamic.config.center.domain.service.IDCCService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
// @RequiredArgsConstructor
@Slf4j
public class DCCBeanPostProcessor implements BeanPostProcessor
{
	@Resource
	private IDCCService idccService;
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
	{
		log.debug("jy-wrench，动态配置-注册，代理对象创建完成。");
		return idccService.proxyObject(bean);
	}
}