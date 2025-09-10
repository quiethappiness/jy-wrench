package com.jingyue.wrench.dynamic.config.center.domain.service;

import com.jingyue.wrench.dynamic.config.center.domain.model.valobj.AttributeVO;

/**
 * IDynamicConfigCenterService
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 动态配置中心服务
 * @date 2025/9/10 16:37
 */
public interface IDynamicConfigCenterService
{
	Object proxyObject(Object bean);
	
	/**
	 * 调整属性值
	 */
	void adjustAttributeValue(AttributeVO attributeVO);
	
}