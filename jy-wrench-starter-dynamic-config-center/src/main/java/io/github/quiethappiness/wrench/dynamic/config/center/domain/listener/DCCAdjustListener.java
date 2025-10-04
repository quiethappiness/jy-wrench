package io.github.quiethappiness.wrench.dynamic.config.center.domain.listener;

import io.github.quiethappiness.wrench.dynamic.config.center.domain.model.valobj.AttributeVO;
import io.github.quiethappiness.wrench.dynamic.config.center.domain.service.IDCCService;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * DCCAdjustListener
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 配置监听器
 * @date 2025/9/10 17:38
 */
@Service
public class DCCAdjustListener implements MessageListener<AttributeVO>
{
	private final Logger log = LoggerFactory.getLogger(DCCAdjustListener.class);
	private final IDCCService dynamicConfigCenterService;
	
	public DCCAdjustListener(
		IDCCService dynamicConfigCenterService)
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