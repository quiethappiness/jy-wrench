package com.jingyue.wrench.dynamic.config.center.listener;

import com.jingyue.wrench.dynamic.config.center.domain.model.valobj.AttributeVO;
import com.jingyue.wrench.dynamic.config.center.domain.service.DynamicConfigCenterServiceImpl;
import com.jingyue.wrench.dynamic.config.center.domain.service.IDynamicConfigCenterService;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DynamicConfigCenterAdjustListener
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 配置监听器
 * @date 2025/9/10 17:38
 */
public class DynamicConfigCenterAdjustListener implements MessageListener<AttributeVO>
{
	private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterAdjustListener.class);
	private final IDynamicConfigCenterService dynamicConfigCenterService;
	
	public DynamicConfigCenterAdjustListener(IDynamicConfigCenterService dynamicConfigCenterService)
	{
		this.dynamicConfigCenterService = dynamicConfigCenterService;
	}
	
	@Override
	public void onMessage(CharSequence channel, AttributeVO attributeVO)
	{
		try
		{
			log.info("jy-wrench dcc config attribute:{} value:{}", attributeVO.getName(), attributeVO.getValue());
			dynamicConfigCenterService.adjustAttributeValue(attributeVO);
		}
		catch (Exception e)
		{
			log.error("jy-wrench dcc config attribute:{} value:{}", attributeVO.getName(), attributeVO.getValue(), e);
		}
	}
}