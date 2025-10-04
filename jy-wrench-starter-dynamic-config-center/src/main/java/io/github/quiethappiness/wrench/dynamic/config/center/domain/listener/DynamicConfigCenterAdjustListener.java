package io.github.quiethappiness.wrench.dynamic.config.center.domain.listener;

import io.github.quiethappiness.wrench.dynamic.config.center.domain.model.valobj.AttributeVO;
import io.github.quiethappiness.wrench.dynamic.config.center.domain.service.IDynamicConfigCenterService;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * DynamicConfigCenterAdjustListener
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 配置监听器
 * @date 2025/9/10 17:38
 */
@Service
public class DynamicConfigCenterAdjustListener implements MessageListener<AttributeVO>
{
	private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterAdjustListener.class);
	private final IDynamicConfigCenterService dynamicConfigCenterService;
	
	public DynamicConfigCenterAdjustListener(
		IDynamicConfigCenterService dynamicConfigCenterService)
	{
		this.dynamicConfigCenterService = dynamicConfigCenterService;
		log.info("jy-wrench，注册器（redis）监听器 dynamicConfigCenterAdjustListener 初始化完成。");
	}
	
	@Override
	public void onMessage(CharSequence channel, AttributeVO attributeVO)
	{
		try
		{
			log.warn("jy-wrench dcc config attribute:{} value:{}", attributeVO.getName(), attributeVO.getValue());
			dynamicConfigCenterService.adjustAttributeValue(attributeVO);
		}
		catch (Exception e)
		{
			log.error("jy-wrench dcc config attribute:{} value:{}", attributeVO.getName(), attributeVO.getValue(), e);
		}
	}
}