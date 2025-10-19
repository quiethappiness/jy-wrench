package io.github.quiethappiness.wrench.util.mq.listener.domain.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * DeadLetterListener
 * @author quietHappiness @jingyue
 * @version 1.0
 * @description 死信监听器
 * @date 2025/8/31 16:32
 */
@Component
@Slf4j
public class DeadLetterListener extends BaseEventListener<String>
{
	@RabbitListener(
		bindings = @QueueBinding(
			exchange = @Exchange(value = "${jy.wrench.util.mq-listener.dl.exchange:dl.exchange}", type = ExchangeTypes.TOPIC),
			value = @Queue(value = "${jy.wrench.util.mq-listener.dl.queue:dl.queue}", durable = "true"),
			key = "${jy.wrench.util.mq-listener.dl.routing_key:dl.routing_key}"
		)
	)
	@Override
	public void handleMessage(String message)
	{
		super.handleMessage(message);
	}
	
	@Override
	protected void processMessage(String message) throws Exception
	{
		log.error("【严重】拼团结算消息处理失败，进入死信队列，需人工介入或补偿: {}", message);
		// 可以发送告警、写数据库、或调用补偿接口
	}
}